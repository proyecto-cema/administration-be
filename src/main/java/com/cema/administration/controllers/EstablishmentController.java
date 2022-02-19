package com.cema.administration.controllers;

import com.cema.administration.constants.Messages;
import com.cema.administration.domain.Establishment;
import com.cema.administration.domain.Subscription;
import com.cema.administration.domain.SubscriptionType;
import com.cema.administration.entities.CemaEstablishment;
import com.cema.administration.entities.CemaSubscription;
import com.cema.administration.entities.CemaSubscriptionType;
import com.cema.administration.exceptions.AlreadyExistsException;
import com.cema.administration.exceptions.NotFoundException;
import com.cema.administration.exceptions.UnauthorizedException;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.mapping.UpdateMappingService;
import com.cema.administration.mapping.impl.SubscriptionMappingService;
import com.cema.administration.repositories.EstablishmentRepository;
import com.cema.administration.repositories.SubscriptionRepository;
import com.cema.administration.repositories.SubscriptionTypeRepository;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.validation.EstablishmentValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
@Api(produces = "application/json", value = "Allows interaction with the establishment database. V1")
@Validated
@Slf4j
public class EstablishmentController {

    private static final String BASE_URL = "/establishment/";

    private final EstablishmentRepository establishmentRepository;
    private final UpdateMappingService<CemaEstablishment, Establishment> establishmentMappingService;
    private final AuthorizationService authorizationService;
    private final EstablishmentValidationService establishmentValidationService;
    private final SubscriptionMappingService subscriptionMappingService;
    private final SubscriptionTypeRepository subscriptionTypeRepository;
    private final SubscriptionRepository subscriptionRepository;

    public EstablishmentController(EstablishmentRepository establishmentRepository, UpdateMappingService<CemaEstablishment, Establishment> establishmentMappingService, AuthorizationService authorizationService, EstablishmentValidationService establishmentValidationService, SubscriptionMappingService subscriptionMappingService, SubscriptionTypeRepository subscriptionTypeRepository, SubscriptionRepository subscriptionRepository) {
        this.establishmentRepository = establishmentRepository;
        this.establishmentMappingService = establishmentMappingService;
        this.authorizationService = authorizationService;
        this.establishmentValidationService = establishmentValidationService;
        this.subscriptionMappingService = subscriptionMappingService;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Register a new establishment to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Establishment created successfully"),
            @ApiResponse(code = 409, message = "The establishment you were trying to create already exists"),
            @ApiResponse(code = 401, message = "You are not allowed to register this establishment")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerEstablishment(
            @ApiParam(
                    value = "Establishment data to be inserted.")
            @RequestBody @Valid Establishment establishment) {

        log.info("Request to register new establishment");

        CemaEstablishment existsEstablishment = establishmentRepository.findCemaEstablishmentByCuig(establishment.getCuig());
        if (existsEstablishment != null) {
            log.info("Establishment cuig already exists");
            throw new AlreadyExistsException(String.format("The establishment with cuig %s already exists", establishment.getCuig()));
        }

        CemaEstablishment newEstablishment = establishmentMappingService.mapDomainToEntity(establishment);

        establishmentRepository.save(newEstablishment);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Register a subscription for an establishment to a subscription type")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Subscription added successfully"),
            @ApiResponse(code = 404, message = "The establishment does not exists"),
            @ApiResponse(code = 404, message = "The subscription does not exists")
    })
    @PostMapping(value = BASE_URL + "{cuig}/subscription", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<Establishment> addSubscription(
            @ApiParam(
                    value = "The cuig of the establishment you are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig,
            @ApiParam(
                    value = "The subscription name to add.", example = "Promo1")
            @RequestParam(value = "name") String name,
            @ApiParam(
                    value = "The date when this subscription becomes active", example = "2022-01-30")
            @RequestParam(value = "startingDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startingDate) {

        log.info("Request to add a subscription to an establishment");

        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            throw new NotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }

        List<CemaSubscriptionType> cemaSubscriptionTypes = subscriptionTypeRepository.findAllByNameIgnoreCase(name);
        Optional<CemaSubscriptionType> cemaSubscriptionTypeOptional = cemaSubscriptionTypes.stream().max(Comparator.comparing(CemaSubscriptionType::getCreationDate));
        if (!cemaSubscriptionTypeOptional.isPresent()) {
            throw new NotFoundException(String.format("SubscriptionType with name %s doesn't exits", name));
        }
        CemaSubscriptionType cemaSubscriptionType = cemaSubscriptionTypeOptional.get();
        if (cemaSubscriptionType.isExpired()) {
            throw new ValidationException(String.format("SubscriptionType with name %s has expired on %s", name, cemaSubscriptionType.getExpirationDate()));
        }

        if (startingDate == null) {
            startingDate = new Date();
        }

        CemaSubscription cemaSubscription = CemaSubscription.builder()
                .cemaSubscriptionType(cemaSubscriptionType)
                .cemaEstablishment(cemaEstablishment)
                .startingDate(startingDate)
                .build();

        subscriptionRepository.save(cemaSubscription);

        Establishment establishment = establishmentMappingService.mapEntityToDomain(cemaEstablishment);

        return new ResponseEntity<>(establishment, HttpStatus.OK);
    }

    @ApiOperation(value = "Validate establishment from cuig sent data")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Establishment is valid"),
            @ApiResponse(code = 404, message = "Establishment not found"),
            @ApiResponse(code = 401, message = "You are not allowed to view this establishment"),
            @ApiResponse(code = 422, message = "Invalid Establishment")
    })
    @GetMapping(value = BASE_URL + "validate/{cuig}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> validateEstablishmentByCuig(
            @ApiParam(
                    value = "The cuig of the establishment you are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig) {

        log.info("Request for establishment with {}", cuig);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            throw new NotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }
        Establishment establishment = establishmentMappingService.mapEntityToDomain(cemaEstablishment);

        establishmentValidationService.validateEstablishmentForUsage(establishment);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retrieve establishment from cuig sent data", response = Establishment.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found establishment"),
            @ApiResponse(code = 404, message = "Establishment not found"),
            @ApiResponse(code = 401, message = "You are not allowed to view this establishment")
    })
    @GetMapping(value = BASE_URL + "{cuig}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Establishment> lookUpEstablishmentByCuig(
            @ApiParam(
                    value = "The cuig of the establishment you are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig) {

        log.info("Request for establishment with {}", cuig);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            throw new NotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }
        Establishment establishment = establishmentMappingService.mapEntityToDomain(cemaEstablishment);

        return new ResponseEntity<>(establishment, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Retrieve subscriptions for an establishment from cuig", response = Subscription.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found subscriptions"),
            @ApiResponse(code = 404, message = "Subscriptions not found"),
            @ApiResponse(code = 401, message = "You are not allowed to view these subscriptions")
    })
    @GetMapping(value = BASE_URL + "{cuig}/subscriptions", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Subscription>> getEstablishmentSubscriptions(
            @ApiParam(
                    value = "The cuig of the establishment you are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig) {

        log.info("Request for subscriptions of establishment with cuig {}", cuig);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            throw new NotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }

        List<CemaSubscription> cemaSubscriptions = cemaEstablishment.getSubscriptions();

        List<Subscription> subscriptions = cemaSubscriptions.stream()
                .map(subscriptionMappingService::mapEntityToDomain)
                .sorted(Comparator.comparing(Subscription::getStartingDate).reversed())
                .collect(Collectors.toList());

        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATRON')")
    @ApiOperation(value = "Modifies an existent Establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Establishment modified successfully"),
            @ApiResponse(code = 404, message = "The establishment you were trying to modify doesn't exists"),
            @ApiResponse(code = 401, message = "You are not allowed to update this establishment")
    })
    @PutMapping(value = BASE_URL + "{cuig}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Establishment> updateEstablishment(
            @ApiParam(
                    value = "The cuig of the establishment we are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig,
            @ApiParam(
                    value = "The establishment data we are modifying. Cuig cannot be modified and will be ignored.")
            @RequestBody Establishment establishment) {

        log.info("Request to modify establishment with cuig: {}", cuig);

        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }
        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            log.info("Establishment doesn't exists");
            throw new NotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }


        cemaEstablishment = establishmentMappingService.updateDomainWithEntity(establishment, cemaEstablishment);

        establishmentRepository.save(cemaEstablishment);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete an existing establishment by cuig")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Establishment deleted successfully"),
            @ApiResponse(code = 404, message = "The establishment you were trying to reach is not found"),
            @ApiResponse(code = 401, message = "You are not allowed to delete this establishment")
    })
    @DeleteMapping(value = BASE_URL + "{cuig}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Establishment> deleteEstablishment(
            @ApiParam(
                    value = "The cuig for the establishment we are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig) {

        log.info("Request to delete user: {}", cuig);

        CemaEstablishment establishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (establishment != null) {
            log.info("Establishment exists, deleting");
            establishmentRepository.delete(establishment);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Not found");
        throw new NotFoundException(String.format("Establishment %s doesn't exits", cuig));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Retrieve all establishments", response = Establishment.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listed all establishments")
    })
    @GetMapping(value = BASE_URL + "list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Establishment>> listEstablishments() {

        List<CemaEstablishment> cemaEstablishments;
        cemaEstablishments = establishmentRepository.findAll();

        List<Establishment> establishments = cemaEstablishments.stream().map(establishmentMappingService::mapEntityToDomain).collect(Collectors.toList());

        return new ResponseEntity<>(establishments, HttpStatus.OK);
    }

}
