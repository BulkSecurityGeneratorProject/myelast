package com.nadee.repository.search;

import com.nadee.domain.User_comment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the User_comment entity.
 */
public interface User_commentSearchRepository extends ElasticsearchRepository<User_comment, Long> {
}
