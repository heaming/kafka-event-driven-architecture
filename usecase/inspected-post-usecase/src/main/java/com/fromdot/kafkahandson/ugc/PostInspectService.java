package com.fromdot.kafkahandson.ugc;

import com.fromdot.kafkahandson.ugc.inspectedpost.AutoInspectionResult;
import com.fromdot.kafkahandson.ugc.inspectedpost.model.InspectedPost;
import com.fromdot.kafkahandson.ugc.port.MetadataPort;
import com.fromdot.kafkahandson.ugc.port.PostAutoInspectPort;
import com.fromdot.kafkahandson.ugc.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PostInspectService implements PostInspectUsecase {

    private final MetadataPort metadataPort;
    private final PostAutoInspectPort postAutoInspectPort;

    @Override
    public InspectedPost inspectAndGetIfValid(Post post) {

        String categoryName = metadataPort.getCategoryNameByCategoryId(post.getCategoryId());
        AutoInspectionResult inspectionResult = postAutoInspectPort.inspect(post, categoryName);

        if(!inspectionResult.getStatus().equals("GOOD")) return null;

        return InspectedPost.generate(
                post,
                categoryName,
                Arrays.asList(inspectionResult.getTags())
        );
    }
}
