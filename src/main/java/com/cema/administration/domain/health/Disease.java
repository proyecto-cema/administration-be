package com.cema.administration.domain.health;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @ApiModelProperty(notes = "The name of the disease", example = "Aftosa")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "Any additional data for this disease", example = "Comprar vacunas")
    private String description;
    @ApiModelProperty(notes = "The cuig this disease is related to", example = "123")
    @NotEmpty(message = "establishmentCuig is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "The the duration in days of this disease", example = "14")
    private Long duration;
}
