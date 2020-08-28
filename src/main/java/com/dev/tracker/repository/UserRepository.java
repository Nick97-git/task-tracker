package com.dev.tracker.repository;

import com.dev.tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.roles Role "
            + "LEFT JOIN FETCH u.tasks Task where u.email = :email")
    User findByEmail(@Param("email") String email);

    void deleteByEmail(String email);

    Page<User> findAll(Pageable pageable);
}
