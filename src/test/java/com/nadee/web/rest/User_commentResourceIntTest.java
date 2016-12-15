package com.nadee.web.rest;

import com.nadee.MyelastApp;

import com.nadee.domain.User_comment;
import com.nadee.repository.User_commentRepository;
import com.nadee.repository.search.User_commentSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the User_commentResource REST controller.
 *
 * @see User_commentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyelastApp.class)
public class User_commentResourceIntTest {

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    @Inject
    private User_commentRepository user_commentRepository;

    @Inject
    private User_commentSearchRepository user_commentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restUser_commentMockMvc;

    private User_comment user_comment;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        User_commentResource user_commentResource = new User_commentResource();
        ReflectionTestUtils.setField(user_commentResource, "user_commentSearchRepository", user_commentSearchRepository);
        ReflectionTestUtils.setField(user_commentResource, "user_commentRepository", user_commentRepository);
        this.restUser_commentMockMvc = MockMvcBuilders.standaloneSetup(user_commentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User_comment createEntity(EntityManager em) {
        User_comment user_comment = new User_comment()
                .comment(DEFAULT_COMMENT);
        return user_comment;
    }

    @Before
    public void initTest() {
        user_commentSearchRepository.deleteAll();
        user_comment = createEntity(em);
    }

    @Test
    @Transactional
    public void createUser_comment() throws Exception {
        int databaseSizeBeforeCreate = user_commentRepository.findAll().size();

        // Create the User_comment

        restUser_commentMockMvc.perform(post("/api/user-comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user_comment)))
                .andExpect(status().isCreated());

        // Validate the User_comment in the database
        List<User_comment> user_comments = user_commentRepository.findAll();
        assertThat(user_comments).hasSize(databaseSizeBeforeCreate + 1);
        User_comment testUser_comment = user_comments.get(user_comments.size() - 1);
        assertThat(testUser_comment.getComment()).isEqualTo(DEFAULT_COMMENT);

        // Validate the User_comment in ElasticSearch
        User_comment user_commentEs = user_commentSearchRepository.findOne(testUser_comment.getId());
        assertThat(user_commentEs).isEqualToComparingFieldByField(testUser_comment);
    }

    @Test
    @Transactional
    public void getAllUser_comments() throws Exception {
        // Initialize the database
        user_commentRepository.saveAndFlush(user_comment);

        // Get all the user_comments
        restUser_commentMockMvc.perform(get("/api/user-comments?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(user_comment.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getUser_comment() throws Exception {
        // Initialize the database
        user_commentRepository.saveAndFlush(user_comment);

        // Get the user_comment
        restUser_commentMockMvc.perform(get("/api/user-comments/{id}", user_comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(user_comment.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUser_comment() throws Exception {
        // Get the user_comment
        restUser_commentMockMvc.perform(get("/api/user-comments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser_comment() throws Exception {
        // Initialize the database
        user_commentRepository.saveAndFlush(user_comment);
        user_commentSearchRepository.save(user_comment);
        int databaseSizeBeforeUpdate = user_commentRepository.findAll().size();

        // Update the user_comment
        User_comment updatedUser_comment = user_commentRepository.findOne(user_comment.getId());
        updatedUser_comment
                .comment(UPDATED_COMMENT);

        restUser_commentMockMvc.perform(put("/api/user-comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUser_comment)))
                .andExpect(status().isOk());

        // Validate the User_comment in the database
        List<User_comment> user_comments = user_commentRepository.findAll();
        assertThat(user_comments).hasSize(databaseSizeBeforeUpdate);
        User_comment testUser_comment = user_comments.get(user_comments.size() - 1);
        assertThat(testUser_comment.getComment()).isEqualTo(UPDATED_COMMENT);

        // Validate the User_comment in ElasticSearch
        User_comment user_commentEs = user_commentSearchRepository.findOne(testUser_comment.getId());
        assertThat(user_commentEs).isEqualToComparingFieldByField(testUser_comment);
    }

    @Test
    @Transactional
    public void deleteUser_comment() throws Exception {
        // Initialize the database
        user_commentRepository.saveAndFlush(user_comment);
        user_commentSearchRepository.save(user_comment);
        int databaseSizeBeforeDelete = user_commentRepository.findAll().size();

        // Get the user_comment
        restUser_commentMockMvc.perform(delete("/api/user-comments/{id}", user_comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean user_commentExistsInEs = user_commentSearchRepository.exists(user_comment.getId());
        assertThat(user_commentExistsInEs).isFalse();

        // Validate the database is empty
        List<User_comment> user_comments = user_commentRepository.findAll();
        assertThat(user_comments).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUser_comment() throws Exception {
        // Initialize the database
        user_commentRepository.saveAndFlush(user_comment);
        user_commentSearchRepository.save(user_comment);

        // Search the user_comment
        restUser_commentMockMvc.perform(get("/api/_search/user-comments?query=id:" + user_comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(user_comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }
}
