package com.cema.administration.controllers;


import com.cema.administration.domain.Establishment;
import com.cema.administration.entities.CemaEstablishment;
import com.cema.administration.exceptions.AlreadyExistsException;
import com.cema.administration.exceptions.NotFoundException;
import com.cema.administration.mapping.UpdateMappingService;
import com.cema.administration.mapping.impl.SubscriptionMappingService;
import com.cema.administration.mapping.impl.SubscriptionTypeMappingService;
import com.cema.administration.repositories.EstablishmentRepository;
import com.cema.administration.repositories.SubscriptionRepository;
import com.cema.administration.repositories.SubscriptionTypeRepository;
import com.cema.administration.services.authorization.AuthorizationService;
import com.cema.administration.services.validation.EstablishmentValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class EstablishmentControllerTest {

    private final String cuig = "321";
    @Mock
    private EstablishmentRepository establishmentRepository;
    @Mock
    private UpdateMappingService mappingService;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private EstablishmentValidationService establishmentValidationService;
    @Mock
    private SubscriptionMappingService subscriptionMappingService;
    @Mock
    private SubscriptionTypeRepository subscriptionTypeRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;


    private EstablishmentController establishmentController;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        when(authorizationService.isOnTheSameEstablishment(cuig)).thenReturn(true);
        when(authorizationService.getCurrentUserCuig()).thenReturn(cuig);
        establishmentController = new EstablishmentController(establishmentRepository, mappingService,
                authorizationService, establishmentValidationService, subscriptionMappingService, subscriptionTypeRepository, subscriptionRepository);
    }

    @Test
    public void lookUpEstablishmentByCuigShouldAlwaysReturnEstablishmentWhenExists() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();
        Establishment establishment = Establishment.builder().build();

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(cemaEstablishment);
        when(mappingService.mapEntityToDomain(cemaEstablishment)).thenReturn(establishment);

        ResponseEntity<Establishment> result = establishmentController.lookUpEstablishmentByCuig(cuig);
        Establishment resultingUser = result.getBody();

        assertThat(resultingUser, is(establishment));
        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void lookUpEstablishmentByCuigShouldAlwaysReturnNotFoundWhenEstablishmentDoesntExists() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();
        Establishment establishment = Establishment.builder().build();

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(null);
        when(mappingService.mapEntityToDomain(cemaEstablishment)).thenReturn(establishment);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            establishmentController.lookUpEstablishmentByCuig(cuig);
        });
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Establishment with cuig 321 doesn't exits"));
    }

    @Test
    public void registerEstablishmentShouldAlwaysReturnCreatedWhenEstablishmentAddedCorrectly() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();
        Establishment establishment = Establishment.builder().build();

        when(mappingService.mapDomainToEntity(establishment)).thenReturn(cemaEstablishment);

        ResponseEntity<String> result = establishmentController.registerEstablishment(establishment);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.CREATED));
    }

    @Test
    public void registerEstablishmentShouldAlwaysReturnUnprocessableEntityWhenEstablishmentExists() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();
        Establishment establishment = Establishment.builder().build();

        cemaEstablishment.setCuig(cuig);
        establishment.setCuig(cuig);

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(cemaEstablishment);
        when(mappingService.mapDomainToEntity(establishment)).thenReturn(cemaEstablishment);

        Exception exception = assertThrows(AlreadyExistsException.class, () -> {
            establishmentController.registerEstablishment(establishment);
        });


        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is(String.format("The establishment with cuig %s already exists", establishment.getCuig())));
    }

    @Test
    public void updateEstablishmentShouldAlwaysReturnOKWhenEstablishmentUpdatedCorrectly() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();
        Establishment establishment = Establishment.builder()
                .cuig(cuig)
                .build();

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(cemaEstablishment);
        when(mappingService.mapDomainToEntity(establishment)).thenReturn(cemaEstablishment);

        ResponseEntity<Establishment> result = establishmentController.updateEstablishment(cuig, establishment);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.OK));
    }

    @Test
    public void updateEstablishmentShouldAlwaysReturnNotFoundWhenEstablishmentDoesntExists() {
        Establishment establishment = Establishment.builder().build();

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(null);


        Exception exception = assertThrows(NotFoundException.class, () ->
                establishmentController.updateEstablishment(cuig, establishment));
        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Establishment with cuig 321 doesn't exits"));
    }

    @Test
    public void deleteShouldAlwaysReturnOKWhenEstablishmentExists() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(cemaEstablishment);


        ResponseEntity<Establishment> result = establishmentController.deleteEstablishment(cuig);

        HttpStatus resultingStatus = result.getStatusCode();

        assertThat(resultingStatus, is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void deleteShouldAlwaysReturnNotFoundWhenUserDoesNotExists() {
        CemaEstablishment cemaEstablishment = new CemaEstablishment();

        when(establishmentRepository.findCemaEstablishmentByCuig(cuig)).thenReturn(cemaEstablishment);


        Exception exception = assertThrows(NotFoundException.class, () -> {
            establishmentController.deleteEstablishment("234");
        });

        String resultingMessage = exception.getMessage();

        assertThat(resultingMessage, is("Establishment 234 doesn't exits"));
    }


}