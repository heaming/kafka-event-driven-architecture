package com.fromdot.kafkahandson.ugc;

import com.fromdot.kafkahandson.ugc.port.SubscribingPostPort;
import com.fromdot.kafkahandson.ugc.post.model.ResolvedPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SubscribingPostListService implements SubscribingPostListUsecase {

    private static final int PAGE_SIZE = 5;

    private final SubscribingPostPort subscribingPostPort;

    private final PostResolvingHelpUsecase postResolvingHelpUsecase;

    @Override
    public List<ResolvedPost> listSubscribingInboxPosts(SubscribingPostListUsecase.Request request) {

        List<Long> subscribingPostIds = subscribingPostPort.listPostIdsByFollowerUserIdWithPagination(
                request.getFollowerUserId(),
                request.getPageNumber(),
                PAGE_SIZE
        );

        return postResolvingHelpUsecase.resolvedPostsByIds(subscribingPostIds);
    }
}
