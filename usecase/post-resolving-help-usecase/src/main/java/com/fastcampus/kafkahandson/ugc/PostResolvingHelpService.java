package com.fastcampus.kafkahandson.ugc;

import com.fastcampus.kafkahandson.ugc.port.MetadataPort;
import com.fastcampus.kafkahandson.ugc.port.PostPort;
import com.fastcampus.kafkahandson.ugc.port.ResolvedPostCachePort;
import com.fastcampus.kafkahandson.ugc.post.model.Post;
import com.fastcampus.kafkahandson.ugc.post.model.ResolvedPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostResolvingHelpService implements PostResolvingHelpUsecase {

    private final PostPort postPort;
    private final MetadataPort metadataPort;
    private final ResolvedPostCachePort resolvedPostCachePort;

    @Override
    public ResolvedPost resolvedPostById(Long postId) {
        ResolvedPost resolvedPost = resolvedPostCachePort.get(postId);

        if(resolvedPost != null) {
            return resolvedPost;
        }

        Post post = postPort.findById(postId);

        if(post != null) {
            String userName = metadataPort.getUserNameByUserId(post.getUserId());
            String categoryName = metadataPort.getCategoryNameByCategoryId(post.getCategoryId());

            if(userName != null && categoryName != null) {
                resolvedPost = ResolvedPost.generate(
                        post,
                        userName,
                        categoryName
                );
                resolvedPostCachePort.set(resolvedPost);
            }
        }

        return resolvedPost;
    }

    @Override
    public List<ResolvedPost> resolvedPostsByIds(List<Long> postIds) { // TODO : 임시
        return postIds.stream().map(this::resolvedPostById).toList();
    }
}
