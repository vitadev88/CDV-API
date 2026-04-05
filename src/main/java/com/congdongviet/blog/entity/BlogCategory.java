package com.congdongviet.blog.entity;

import com.congdongviet.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blog_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategory extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;
}
