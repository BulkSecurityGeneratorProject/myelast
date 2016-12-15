package com.nadee.repository;

import com.nadee.domain.User_comment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the User_comment entity.
 */
@SuppressWarnings("unused")
public interface User_commentRepository extends JpaRepository<User_comment,Long> {

}
