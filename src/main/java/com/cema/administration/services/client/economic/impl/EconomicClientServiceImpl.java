package com.cema.administration.services.client.economic.impl;

import com.cema.administration.domain.ErrorResponse;
import com.cema.administration.domain.bovine.Bovine;
import com.cema.administration.domain.economic.BovineOperation;
import com.cema.administration.domain.economic.Supply;
import com.cema.administration.domain.economic.SupplyOperation;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.client.economic.EconomicClientService;
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
public class EconomicClientServiceImpl implements EconomicClientService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_GET_SUPPLY = "supply/{name}";
    private static final String PATH_GET_ALL_SUPPLY_OPERATIONS = "/supply-operations/list?size=999";
    private static final String PATH_GET_ALL_BOVINE_OPERATIONS = "/bovine-operations/list?size=999";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public EconomicClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.economic.url}") String url, AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public Supply getSupply(String food) {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_GET_SUPPLY;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<Supply> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, Supply.class, food);
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }

    @SneakyThrows
    @Override
    public List<SupplyOperation> getAllSupplyOperations() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_GET_ALL_SUPPLY_OPERATIONS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<SupplyOperation>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<SupplyOperation>>() {});
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }

    @SneakyThrows
    @Override
    public List<BovineOperation> getAllBovineOperations() {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_GET_ALL_BOVINE_OPERATIONS;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<List<BovineOperation>> responseEntity = restTemplate.exchange(searchUrl, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<BovineOperation>>() {});
            return responseEntity.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }
}
