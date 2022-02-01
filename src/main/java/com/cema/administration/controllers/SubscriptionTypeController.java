package com.cema.administration.controllers;

import com.cema.administration.domain.SubscriptionType;
import com.cema.administration.entities.CemaSubscriptionType;
import com.cema.administration.exceptions.AlreadyExistsException;
import com.cema.administration.exceptions.NotFoundException;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.mapping.UpdateMappingService;
import com.cema.administration.repositories.SubscriptionTypeRepository;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.database.DatabaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the subscription database. V1")
@Validated
@Slf4j
public class SubscriptionTypeController {

    private static final String BASE_URL = "/subscription/";

    private final SubscriptionTypeRepository subscriptionTypeRepository;
    private final UpdateMappingService<CemaSubscriptionType, SubscriptionType> subscriptionTypeMappingService;
    private final DatabaseService databaseService;
    private final AuthorizationService authorizationService;

    public SubscriptionTypeController(SubscriptionTypeRepository subscriptionTypeRepository, UpdateMappingService<CemaSubscriptionType, SubscriptionType> subscriptionTypeMappingService, DatabaseService databaseService, AuthorizationService authorizationService) {
        this.subscriptionTypeRepository = subscriptionTypeRepository;
        this.subscriptionTypeMappingService = subscriptionTypeMappingService;
        this.databaseService = databaseService;
        this.authorizationService = authorizationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Register a new subscriptionType to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "SubscriptionType created successfully"),
            @ApiResponse(code = 409, message = "The subscriptionType you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this subscriptionType")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerSubscriptionType(
            @ApiParam(
                    value = "SubscriptionType data to be inserted.")
            @RequestBody @Valid SubscriptionType subscriptionType) {

        log.info("Request to register new subscriptionType");

        List<CemaSubscriptionType> cemaSubscriptionTypes = subscriptionTypeRepository.findAllByNameIgnoreCase(subscriptionType.getName());
        Optional<CemaSubscriptionType> cemaSubscriptionTypeOptional = cemaSubscriptionTypes.stream().max(Comparator.comparing(CemaSubscriptionType::getCreationDate));
        if (cemaSubscriptionTypeOptional.isPresent() && !cemaSubscriptionTypeOptional.get().isExpired()) {
            log.info("There is already an active SubscriptionType with that name");
            throw new AlreadyExistsException(String.format("TThere is already an active SubscriptionType with name %s", subscriptionType.getName()));
        }

        CemaSubscriptionType newSubscriptionType = subscriptionTypeMappingService.mapDomainToEntity(subscriptionType);

        subscriptionTypeRepository.save(newSubscriptionType);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @ApiOperation(value = "Validate subscriptionType by name", response = SubscriptionType.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "SubscriptionType is valid"),
            @ApiResponse(code = 404, message = "SubscriptionType not found")
    })
    @GetMapping(value = BASE_URL + "validate/{name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> validateSubscriptionTypeByCuig(
            @ApiParam(
                    value = "The name of the subscriptionType you are looking for.",
                    example = "123")
            @PathVariable("name") String name) {

        log.info("Request for subscriptionType with {}", name);


        List<CemaSubscriptionType> cemaSubscriptionTypes = subscriptionTypeRepository.findAllByNameIgnoreCase(name);
        Optional<CemaSubscriptionType> cemaSubscriptionTypeOptional = cemaSubscriptionTypes.stream().max(Comparator.comparing(CemaSubscriptionType::getCreationDate));
        if (!cemaSubscriptionTypeOptional.isPresent()) {
            throw new NotFoundException(String.format("SubscriptionType with name %s doesn't exits", name));
        }
        CemaSubscriptionType cemaSubscriptionType = cemaSubscriptionTypeOptional.get();
        if (cemaSubscriptionType.isExpired()) {
            throw new ValidationException(String.format("SubscriptionType with name %s has expired on %s", name, cemaSubscriptionType.getExpirationDate()));
        }

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Retrieve subscriptionType from name sent data", response = SubscriptionType.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found subscriptionType"),
            @ApiResponse(code = 404, message = "SubscriptionType not found")
    })
    @GetMapping(value = BASE_URL + "{name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SubscriptionType> lookUpSubscriptionTypeByCuig(
            @ApiParam(
                    value = "The name of the subscriptionType you are looking for.",
                    example = "123")
            @PathVariable("name") String name) {

        log.info("Request for subscriptionType with {}", name);

        List<CemaSubscriptionType> cemaSubscriptionTypes = subscriptionTypeRepository.findAllByNameIgnoreCase(name);
        Optional<CemaSubscriptionType> cemaSubscriptionTypeOptional = cemaSubscriptionTypes.stream().max(Comparator.comparing(CemaSubscriptionType::getCreationDate));
        if (!cemaSubscriptionTypeOptional.isPresent()) {
            throw new NotFoundException(String.format("SubscriptionType with name %s doesn't exits", name));
        }
        CemaSubscriptionType cemaSubscriptionType = cemaSubscriptionTypeOptional.get();
        SubscriptionType subscriptionType = subscriptionTypeMappingService.mapEntityToDomain(cemaSubscriptionType);

        return new ResponseEntity<>(subscriptionType, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Modifies an existent SubscriptionType")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SubscriptionType modified successfully"),
            @ApiResponse(code = 404, message = "The subscriptionType you were trying to modify doesn't exists")
    })
    @PutMapping(value = BASE_URL + "{name}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SubscriptionType> updateSubscriptionType(
            @ApiParam(
                    value = "The name of the subscriptionType we are looking for.",
                    example = "123")
            @PathVariable("name") String name,
            @ApiParam(
                    value = "The subscriptionType data we are modifying. Cuig cannot be modified and will be ignored.")
            @RequestBody SubscriptionType subscriptionType) {

        log.info("Request to modify subscriptionType with name: {}", name);

        List<CemaSubscriptionType> cemaSubscriptionTypes = subscriptionTypeRepository.findAllByNameIgnoreCase(name);
        Optional<CemaSubscriptionType> cemaSubscriptionTypeOptional = cemaSubscriptionTypes.stream().max(Comparator.comparing(CemaSubscriptionType::getCreationDate));
        if (!cemaSubscriptionTypeOptional.isPresent()) {
            log.info("SubscriptionType doesn't exists");
            throw new NotFoundException(String.format("SubscriptionType with name %s doesn't exits", name));
        }
        CemaSubscriptionType cemaSubscriptionType = cemaSubscriptionTypeOptional.get();
        cemaSubscriptionType = subscriptionTypeMappingService.updateDomainWithEntity(subscriptionType, cemaSubscriptionType);

        CemaSubscriptionType cemaSubscriptionTypeUpdated = subscriptionTypeRepository.save(cemaSubscriptionType);
        SubscriptionType subscriptionTypeUpdated = subscriptionTypeMappingService.mapEntityToDomain(cemaSubscriptionTypeUpdated);

        return ResponseEntity.ok().body(subscriptionTypeUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Invalidate an existing subscriptionType by name")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "SubscriptionType invalidated successfully"),
            @ApiResponse(code = 404, message = "The subscriptionType is not found")
    })
    @DeleteMapping(value = BASE_URL + "{name}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SubscriptionType> deleteSubscriptionType(
            @ApiParam(
                    value = "The name for the subscriptionType we are looking for.",
                    example = "123")
            @PathVariable("name") String name) {

        log.info("Request to delete user: {}", name);

        List<CemaSubscriptionType> cemaSubscriptionTypes = subscriptionTypeRepository.findAllByNameIgnoreCase(name);
        Optional<CemaSubscriptionType> cemaSubscriptionTypeOptional = cemaSubscriptionTypes.stream().max(Comparator.comparing(CemaSubscriptionType::getCreationDate));
        if (cemaSubscriptionTypeOptional.isPresent()) {
            log.info("SubscriptionType exists, invalidating");
            CemaSubscriptionType cemaSubscriptionType = cemaSubscriptionTypeOptional.get();
            cemaSubscriptionType.setExpirationDate(new Date());
            subscriptionTypeRepository.save(cemaSubscriptionType);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Not found");
        throw new NotFoundException(String.format("SubscriptionType %s doesn't exits", name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Retrieve a list of supplies matching the sent data", response = SubscriptionType.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found supplies", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @PostMapping(value = BASE_URL + "search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<SubscriptionType>> searchSupplies(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of supplies to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "3") int size,
            @ApiParam(
                    value = "The subscriptionType data we are searching")
            @RequestBody SubscriptionType subscriptionType) {

        CemaSubscriptionType cemaSubscriptionType = subscriptionTypeMappingService.mapDomainToEntity(subscriptionType);

        Page<CemaSubscriptionType> cemaSubscriptionTypePage = databaseService.searchSubscriptionTypes(cemaSubscriptionType, page, size);

        List<CemaSubscriptionType> cemaSubscriptionTypes = cemaSubscriptionTypePage.getContent();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-elements", String.valueOf(cemaSubscriptionTypePage.getTotalElements()));
        responseHeaders.set("total-pages", String.valueOf(cemaSubscriptionTypePage.getTotalPages()));
        responseHeaders.set("current-page", String.valueOf(cemaSubscriptionTypePage.getNumber()));

        List<SubscriptionType> subscriptionTypes = cemaSubscriptionTypes.stream().map(subscriptionTypeMappingService::mapEntityToDomain).collect(Collectors.toList());

        return ResponseEntity.ok().headers(responseHeaders).body(subscriptionTypes);
    }
}
