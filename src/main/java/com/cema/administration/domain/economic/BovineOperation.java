package com.cema.administration.domain.economic;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BovineOperation {
    @ApiModelProperty(notes = "The transaction Id of this operation, autogenerated.", example = "b000bba4-229e-4b59-8548-1c26508e459c")
    private UUID id;
    @ApiModelProperty(notes = "The bovine this operation is for", example = "12234")
    @NotEmpty(message = "Tag is required")
    private String bovineTag;
    @ApiModelProperty(notes = "Any additional data for this operation", example = "Realizada en efectivo")
    private String description;
    @ApiModelProperty(notes = "The cuig this operation is related to", example = "123")
    @NotEmpty(message = "establishmentCuig is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "The amount of money this operation costs", example = "5432")
    @NotNull
    private Long amount;
    @ApiModelProperty(notes = "If this is a buy operation, the name of the person who sold us the animal", example = "Roberto")
    private String sellerName;
    @ApiModelProperty(notes = "If this is a sell operation, the name of the person who bought us the animal", example = "Roberto")
    private String buyerName;
    @ApiModelProperty(notes = "The type of operation", example = "buy|sell")
    @NotEmpty(message = "type is required")
    @Pattern(regexp = "(?i)buy|sell")
    private String operationType;
    @ApiModelProperty(notes = "The the username of the operator who created this operation", example = "merlinds")
    @NotEmpty(message = "operator username is required")
    private String operatorUserName;
    @ApiModelProperty(notes = "The date when this operation took place", example = "2021-02-12")
    @NotNull
    private Date transactionDate;
}
