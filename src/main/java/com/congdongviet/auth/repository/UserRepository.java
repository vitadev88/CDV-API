package com.congdongviet.auth.repository;

import com.congdongviet.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role <> com.congdongviet.auth.entity.Role.SUPER_ADMIN AND " +
           "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR u.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);

    /** [year, month, count] — số user đăng ký theo tháng kể từ :since */
    @Query("SELECT FUNCTION('YEAR', u.createdAt), FUNCTION('MONTH', u.createdAt), COUNT(u) " +
           "FROM User u WHERE u.createdAt >= :since " +
           "GROUP BY FUNCTION('YEAR', u.createdAt), FUNCTION('MONTH', u.createdAt) " +
           "ORDER BY FUNCTION('YEAR', u.createdAt), FUNCTION('MONTH', u.createdAt)")
    List<Object[]> countByMonth(@Param("since") LocalDateTime since);
}
