package com.nadee.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nadee.domain.User_comment;

import com.nadee.repository.User_commentRepository;
import com.nadee.repository.search.User_commentSearchRepository;
import com.nadee.web.rest.util.HeaderUtil;
import com.nadee.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing User_comment.
 */
@RestController
@RequestMapping("/api")
public class User_commentResource {

    private final Logger log = LoggerFactory.getLogger(User_commentResource.class);
        
    @Inject
    private User_commentRepository user_commentRepository;

    @Inject
    private User_commentSearchRepository user_commentSearchRepository;

    /**
     * POST  /user-comments : Create a new user_comment.
     *
     * @param user_comment the user_comment to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user_comment, or with status 400 (Bad Request) if the user_comment has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-comments")
    @Timed
    public ResponseEntity<User_comment> createUser_comment(@Valid @RequestBody User_comment user_comment) throws URISyntaxException {
        log.debug("REST request to save User_comment : {}", user_comment);
        if (user_comment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user_comment", "idexists", "A new user_comment cannot already have an ID")).body(null);
        }
        User_comment result = user_commentRepository.save(user_comment);
        user_commentSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/user-comments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("user_comment", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-comments : Updates an existing user_comment.
     *
     * @param user_comment the user_comment to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user_comment,
     * or with status 400 (Bad Request) if the user_comment is not valid,
     * or with status 500 (Internal Server Error) if the user_comment couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-comments")
    @Timed
    public ResponseEntity<User_comment> updateUser_comment(@Valid @RequestBody User_comment user_comment) throws URISyntaxException {
        log.debug("REST request to update User_comment : {}", user_comment);
        if (user_comment.getId() == null) {
            return createUser_comment(user_comment);
        }
        User_comment result = user_commentRepository.save(user_comment);
        user_commentSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("user_comment", user_comment.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-comments : get all the user_comments.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of user_comments in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/user-comments")
    @Timed
    public ResponseEntity<List<User_comment>> getAllUser_comments(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of User_comments");
        Page<User_comment> page = user_commentRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-comments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-comments/:id : get the "id" user_comment.
     *
     * @param id the id of the user_comment to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user_comment, or with status 404 (Not Found)
     */
    @GetMapping("/user-comments/{id}")
    @Timed
    public ResponseEntity<User_comment> getUser_comment(@PathVariable Long id) {
        log.debug("REST request to get User_comment : {}", id);
        User_comment user_comment = user_commentRepository.findOne(id);
        return Optional.ofNullable(user_comment)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /user-comments/:id : delete the "id" user_comment.
     *
     * @param id the id of the user_comment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-comments/{id}")
    @Timed
    public ResponseEntity<Void> deleteUser_comment(@PathVariable Long id) {
        log.debug("REST request to delete User_comment : {}", id);
        user_commentRepository.delete(id);
        user_commentSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("user_comment", id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-comments?query=:query : search for the user_comment corresponding
     * to the query.
     *
     * @param query the query of the user_comment search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/user-comments")
    @Timed
    public ResponseEntity<List<User_comment>> searchUser_comments(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of User_comments for query {}", query);
        Page<User_comment> page = user_commentSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-comments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
