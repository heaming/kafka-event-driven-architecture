package com.fromdot.kafkahandson.ugc.subscribepost;

import com.fromdot.kafkahandson.ugc.port.SubscribingPostPort;
import com.fromdot.kafkahandson.ugc.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SubscribingPostAdapter implements SubscribingPostPort {

    private final SubscribingPostRepository subscribingPostRepository;

    @Override
    public void addPostToFollowerInboxed(Post post, List<Long> followerUserIds) {

        List<SubscribingPostDocument> documents = followerUserIds.stream().map(
                followerUserId -> SubscribingPostDocument.generate(post, followerUserId)
        ).toList();

        subscribingPostRepository.saveAll(documents);
    }

    @Override
    public void removePostFromFollowerInboxes(Long postId) {
        subscribingPostRepository.deleteAllByPostId(postId);
    }

    @Override
    public List<Long> listPostIdsByFollowerUserIdWithPagination(Long followerUserId, int pageNumber, int pageSize) {

        List<SubscribingPostDocument> documents = subscribingPostRepository.findByFollowerUserIdWithPagination(followerUserId, pageNumber, pageSize);

        return documents.stream().map(SubscribingPostDocument::getPostId).toList();
    }

}
