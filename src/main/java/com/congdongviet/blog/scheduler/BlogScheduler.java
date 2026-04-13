package com.congdongviet.blog.scheduler;

import com.congdongviet.blog.entity.BlogPost;
import com.congdongviet.blog.entity.BlogStatus;
import com.congdongviet.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs every 60 seconds and auto-publishes blog posts whose scheduled time has arrived.
 *
 * Flow:
 *   Admin saves post with status=SCHEDULED + scheduledAt=<future datetime>
 *   → Scheduler wakes up, finds posts WHERE status=SCHEDULED AND scheduledAt <= NOW()
 *   → Sets status=PUBLISHED, publishedAt=NOW(), scheduledAt=NULL
 *   → Post appears live on the website
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BlogScheduler {

    private final BlogPostRepository blogPostRepository;

    @Scheduled(fixedDelay = 60_000)   // every 60 seconds
    @Transactional
    public void publishScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<BlogPost> due = blogPostRepository.findScheduledDue(BlogStatus.SCHEDULED, now);

        if (due.isEmpty()) return;

        log.info("[BlogScheduler] Auto-publishing {} post(s) at {}", due.size(), now);

        for (BlogPost post : due) {
            post.setStatus(BlogStatus.PUBLISHED);
            post.setPublishedAt(now);
            post.setScheduledAt(null);
            blogPostRepository.save(post);
            log.info("[BlogScheduler] Published: '{}' (id={})", post.getTitle(), post.getId());
        }
    }
}
