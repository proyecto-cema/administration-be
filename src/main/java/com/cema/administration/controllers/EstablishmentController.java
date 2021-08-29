package com.cema.administration.controllers;

import com.cema.administration.domain.Establishment;
import com.cema.administration.entities.CemaEstablishment;
import com.cema.administration.exceptions.EstablishmentAlreadyExistsException;
import com.cema.administration.exceptions.EstablishmentNotFoundException;
import com.cema.administration.mapping.EstablishmentMapping;
import com.cema.administration.repositories.EstablishmentRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the establishment database. V1")
public class EstablishmentController {

    private static final String BASE_URL = "/establishment/";

    private final Logger LOG = LoggerFactory.getLogger(EstablishmentController.class);

    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentMapping establishmentMapping;

    public EstablishmentController(EstablishmentRepository establishmentRepository, EstablishmentMapping establishmentMapping) {
        this.establishmentRepository = establishmentRepository;
        this.establishmentMapping = establishmentMapping;
    }

    @ApiOperation(value = "Register a new establishment to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Establishment created successfully"),
            @ApiResponse(code = 409, message = "The establishment you were trying to create already exists")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerEstablishment(
            @ApiParam(
                    value = "Establishment data to be inserted.")
            @RequestBody Establishment establishment) {

        LOG.info("Request to register new establishment");

        CemaEstablishment existsEstablishment = establishmentRepository.findCemaEstablishmentByCuig(establishment.getCuig());
        if (existsEstablishment != null) {
            LOG.info("Establishment cuig already exists");
            throw new EstablishmentAlreadyExistsException(String.format("The establishment with cuig %s already exists", establishment.getCuig()));
        }

        CemaEstablishment newEstablishment = establishmentMapping.mapDomainToEntity(establishment);

        establishmentRepository.save(newEstablishment);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Retrieve establishment from cuig sent data", response = Establishment.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found establishment"),
            @ApiResponse(code = 404, message = "Establishment not found")
    })
    @GetMapping(value = BASE_URL + "{cuig}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Establishment> lookUpEstablishmentByCuig(
            @ApiParam(
                    value = "The cuig of the establishment you are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig) {

        LOG.info("Request for establishment with {}", cuig);

        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            throw new EstablishmentNotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }
        Establishment establishment = establishmentMapping.mapEntityToDomain(cemaEstablishment);

        return new ResponseEntity<>(establishment, HttpStatus.OK);
    }

    @ApiOperation(value = "Modifies an existent Establishment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Establishment modified successfully"),
            @ApiResponse(code = 404, message = "The establishment you were trying to modify doesn't exists")
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

        LOG.info("Request to modify establishment with cuig: {}", cuig);

        CemaEstablishment cemaEstablishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (cemaEstablishment == null) {
            LOG.info("Establishment doesn't exists");
            throw new EstablishmentNotFoundException(String.format("Establishment with cuig %s doesn't exits", cuig));
        }

        cemaEstablishment = establishmentMapping.updateDomainWithEntity(establishment, cemaEstablishment);

        establishmentRepository.save(cemaEstablishment);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing establishment by cuig")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Establishment deleted successfully"),
            @ApiResponse(code = 404, message = "The establishment you were trying to reach is not found")
    })
    @DeleteMapping(value = BASE_URL + "{cuig}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Establishment> deleteEstablishment(
            @ApiParam(
                    value = "The cuig for the establishment we are looking for.",
                    example = "123")
            @PathVariable("cuig") String cuig) {

        LOG.info("Request to delete user: {}", cuig);

        CemaEstablishment establishment = establishmentRepository.findCemaEstablishmentByCuig(cuig);
        if (establishment != null) {
            LOG.info("Establishment exists, deleting");
            establishmentRepository.delete(establishment);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        LOG.info("Not found");
        throw new EstablishmentNotFoundException(String.format("Establishment %s doesn't exits", cuig));
    }

}
