package com.cema.administration.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class Establishment {
    @ApiModelProperty(notes = "The name of this establishment", example = "La Hermosa")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "The geographical location of this establishment", example = "Kilometro 14")
    private String location;
    @ApiModelProperty(notes = "The official id of this establishment, granted by senasa", example = "123")
    @NotEmpty(message = "CUIG is required")
    private String cuig;
    @ApiModelProperty(notes = "The phone number to contact this establishment", example = "+543541330188")
    private String phone;
    @ApiModelProperty(notes = "The email address to contact this establishment", example = "lahermosa@cema.com.ar")
    private String email;
    @ApiModelProperty(notes = "The the username of the owner of this establishment", example = "merlinds")
    @NotEmpty(message = "OwnerName is required")
    private String ownerUserName;
}
