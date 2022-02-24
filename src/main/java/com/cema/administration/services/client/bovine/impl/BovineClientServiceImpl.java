package com.cema.administration.services.client.bovine.impl;

import com.cema.administration.domain.ErrorResponse;
import com.cema.administration.domain.bovine.Batch;
import com.cema.administration.domain.bovine.Bovine;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.client.bovine.BovineClientService;
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
public class BovineClientServiceImpl implements BovineClientService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_LIST_ALL_BOVINES = "bovines/search?size=999";
    private static final String PATH_GET_BOVINE = "bovines/{tag}";
    private static final String PATH_LIST_ALL_BATCHES = "batches/list";
    private static final String PATH_LIST_ALL_BOVINES_FROM_TAGS = "bovines/list";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public BovineClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.bovine.url}") String url,
                                   AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public Bovine getBovine(String tag) {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_GET_BOVINE;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        HttpEntity entity = new HttpEntity(httpHeaders);
        try {
            ResponseEntity<Bovine> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, Bovine.class, tag);
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

    @SneakyThrows
    @Override
    public List<Bovine> getAllBovines() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_LIST_ALL_BOVINES;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        HttpEntity entity = new HttpEntity(httpHeaders);
        try {
        ResponseEntity<List<Bovine>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<Bovine>>() {});
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

    @SneakyThrows
    @Override
    public List<Batch> getAllBatches(){
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_LIST_ALL_BATCHES;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<Batch>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Batch>>() {
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

    @SneakyThrows
    @Override
    public List<Bovine> getAllBovinesFromList(List<String> tags){
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_LIST_ALL_BOVINES_FROM_TAGS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> entity = new HttpEntity<>(tags, httpHeaders);
        try {
            ResponseEntity<List<Bovine>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Bovine>>() {
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
