package com.cema.administration.domain.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @ApiModelProperty(notes = "The name for this location", example = "Corral 5")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "The description for this location", example = "Grande y con mucho pasto")
    private String description;
    @ApiModelProperty(notes = "The size of this location", example = "30")
    private Long size;
    @ApiModelProperty(notes = "The cuig of the establishment this location belongs to.", example = "321")
    @NotEmpty(message = "Cuig is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "True if this is the default location where bovines are sent. False by default", example = "False")
    private Boolean isDefault;
}
