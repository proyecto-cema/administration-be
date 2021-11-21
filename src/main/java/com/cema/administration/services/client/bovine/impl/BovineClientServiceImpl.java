package com.cema.administration.services.client.bovine.impl;

import com.cema.administration.domain.bovine.Bovine;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.client.bovine.BovineClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BovineClientServiceImpl implements BovineClientService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH = "bovines/search?size=999";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;

    public BovineClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.bovine.url}") String url,
                                   AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @Override
    public List<Bovine> getAllBovines() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        HttpEntity entity = new HttpEntity(httpHeaders);
        ResponseEntity<List<Bovine>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Bovine>>() {
        });
        return responseEntity.getBody();
    }
}
