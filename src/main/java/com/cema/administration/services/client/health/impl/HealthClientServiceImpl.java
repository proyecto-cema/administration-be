package com.cema.administration.services.client.health.impl;

import com.cema.administration.domain.ErrorResponse;
import com.cema.administration.domain.health.Illness;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.client.health.HealthClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HealthClientServiceImpl implements HealthClientService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_LIST_ILLNESS = "illness/list?size=9999&page=0";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public HealthClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.health.url}") String url, AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public List<Illness> getAllBovineIllness() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_LIST_ILLNESS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<Illness>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Illness>>() {
            });
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            if (httpClientErrorException.getRawStatusCode() == 404) {
                return null;
            }
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }
}
