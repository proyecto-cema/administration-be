package com.cema.administration.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
public class SubscriptionType {

    @ApiModelProperty(notes = "The name of this subscription type", example = "Promo 3 Meses")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "The date when this subscription type was created")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date creationDate;
    @ApiModelProperty(notes = "The date when this subscription type is set to expire")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date expirationDate;
    @ApiModelProperty(notes = "The description of this subscription type", example = "Promocion pagando 3 meses juntos")
    private String description;
    @ApiModelProperty(notes = "The price of this subscription", example = "2000")
    @Min(value = 0L, message = "The price must be positive")
    @NotNull(message = "Price is required")
    private Long price;
    @ApiModelProperty(notes = "The duration of this subscription", example = "60")
    @Min(value = 0L, message = "The duration must be positive")
    @NotNull(message = "Duration is required")
    private Long duration;
}
