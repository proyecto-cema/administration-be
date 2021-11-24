package com.cema.administration.services.client.activity.impl;

import com.cema.administration.domain.activity.Ultrasound;
import com.cema.administration.domain.activity.Weighing;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.client.activity.ActivityClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ActivityClientServiceImpl implements ActivityClientService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_ULTRASOUND = "ultrasounds/search?size=999";
    private static final String PATH_WEIGHTINGS = "weightings/search?size=999";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;

    public ActivityClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.activity.url}") String url,
                                     AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @Override
    public List<Ultrasound> getAllUltrasounds() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_ULTRASOUND;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}",httpHeaders);
        ResponseEntity<List<Ultrasound>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Ultrasound>>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public List<Weighing> getAllWeightings() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_WEIGHTINGS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}",httpHeaders);
        ResponseEntity<List<Weighing>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Weighing>>() {
        });
        return responseEntity.getBody();
    }
}
