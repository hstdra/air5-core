package com.example.air5core.services;

import com.example.air5core.models.request.UserPointRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;

@Service
public class AdapterService {
    @Value("${air5-ml.host}")
    private String mlHost;

    @Async
    public void updateUserPoint(UserPointRequest userPointRequest) {
        String url = String.format("%s/%s", mlHost, userPointRequest.getUserId());
        HttpEntity<UserPointRequest> entity = new HttpEntity<>(userPointRequest);
        new AsyncRestTemplate().put(url, entity);
    }
}
