package com.cema.administration.domain.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Inoculation extends Activity {

    @ApiModelProperty(notes = "The dose inoculated", example = "5000")
    private Long dose;
    @ApiModelProperty(notes = "The brand of the drug used", example = "MERCK")
    private String brand;
    @ApiModelProperty(notes = "The drug inoculated", example = "fenbendazole")
    @NotEmpty(message = "Drug is required")
    private String drug;
    @ApiModelProperty(notes = "The product used", example = "safe-guard")
    private String product;
    @ApiModelProperty(notes = "The tag this activity is associated to. Either this or batchName must be populated", example = "1234")
    private String bovineTag;
    @ApiModelProperty(notes = "The batch this activity is associated to. Either this or bovineTag must be populated", example = "the_batch")
    private String batchName;

    @Builder
    public Inoculation(UUID id, @NotEmpty(message = "Name is required") String name,
                       @NotEmpty(message = "Type is required") @Pattern(regexp = "(?i)inoculation|feeding|weighing|ultrasound|movement") String type,
                       String description, Date executionDate,
                       @NotEmpty(message = "Cuig is required") String establishmentCuig, Long dose, String brand,
                       String drug, String product, String bovineTag, String batchName, String workerUserName) {
        super(id, name, type, description, executionDate, establishmentCuig, workerUserName);
        this.dose = dose;
        this.brand = brand;
        this.drug = drug;
        this.product = product;
        this.bovineTag = bovineTag;
        this.batchName = batchName;
    }
}
