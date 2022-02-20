package com.cema.administration.services.client.activity.impl;

import com.cema.administration.domain.ErrorResponse;
import com.cema.administration.domain.activity.Feeding;
import com.cema.administration.domain.activity.Ultrasound;
import com.cema.administration.domain.activity.Weighing;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.client.activity.ActivityClientService;
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
public class ActivityClientServiceImpl implements ActivityClientService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_ULTRASOUND = "ultrasounds/search?size=999";
    private static final String PATH_WEIGHTINGS = "weightings/search?size=999";
    private static final String PATH_FEEDINGS = "feedings/search?size=999";
    private static final String PATH_WEIGHTINGS_LAST = "weightings/search?size=10";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ActivityClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.activity.url}") String url,
                                     AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public List<Ultrasound> getAllUltrasounds() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_ULTRASOUND;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<Ultrasound>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Ultrasound>>() {
            });
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }

    @SneakyThrows
    @Override
    public List<Weighing> getAllWeightings() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_WEIGHTINGS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<Weighing>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Weighing>>() {
            });
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }

    @SneakyThrows
    @Override
    public List<Feeding> getAllFeedings() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_FEEDINGS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<Feeding>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Feeding>>() {
            });
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }

    @SneakyThrows
    @Override
    public List<Weighing> getLastWeightingsForBovine(String bovineTag) {
        Weighing toSearch = Weighing.builder()
                .bovineTag(bovineTag)
                .build();
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_WEIGHTINGS_LAST;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Weighing> entity = new HttpEntity<>(toSearch, httpHeaders);
        try {
            ResponseEntity<List<Weighing>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<List<Weighing>>() {
            });
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }
}
