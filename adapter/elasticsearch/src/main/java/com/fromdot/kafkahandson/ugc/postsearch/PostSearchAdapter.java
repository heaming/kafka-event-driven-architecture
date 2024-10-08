package com.fromdot.kafkahandson.ugc.postsearch;

import com.fromdot.kafkahandson.ugc.inspectedpost.model.InspectedPost;
import com.fromdot.kafkahandson.ugc.port.PostSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PostSearchAdapter implements PostSearchPort {

    private final PostSearchRepository postSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void indexPost(InspectedPost post) {
        postSearchRepository.save(this.toDocument(post));
    }

    @Override
    public void deletePost(Long id) {
        postSearchRepository.deleteById(id);
    }

    @Override
    public List<Long> searchPostIdsByKeyword(String keyword, int pageNumber, int pageSize) {
        if(keyword == null || keyword.isBlank() || pageNumber < 0 || pageSize < 0) {
            return List.of();
        }

        Query query = buildQuery(keyword, pageNumber, pageSize);
        SearchHits<PostDocument> search = elasticsearchOperations.search(query, PostDocument.class);

        return search.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(PostDocument::getId)
                .toList();
    }

    private Query buildQuery(String keyword, int pageNumber, int pageSize) {
        Criteria criteria = new Criteria("title").matches(keyword)
                .or(new Criteria("content").matches(keyword))
                .or(new Criteria("autoGeneratedTags").is(keyword))
                .or(new Criteria("categoryName").is(keyword));

        return new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(pageNumber, pageSize));
    }

    private PostDocument toDocument(InspectedPost post) {
        return new PostDocument(
                post.getPost().getId(),
                post.getPost().getTitle(),
                post.getPost().getContent(),
                post.getCategoryName(),
                post.getAutoGeneratedTags(),
                LocalDateTime.now()
        );
    }
}
