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
public class Feeding extends Activity {

    @ApiModelProperty(notes = "The type of food used for this activity", example = "Heno")
    private String food;
    @ApiModelProperty(notes = "The amount of food used", example = "500")
    private Long amount;
    @ApiModelProperty(notes = "The tag this activity is associated to", example = "1234")
    @NotEmpty(message = "Bovine Tag is required")
    private String bovineTag;

    @Builder
    public Feeding(UUID id, @NotEmpty(message = "Name is required") String name,
                   @NotEmpty(message = "Type is required")
                   @Pattern(regexp = "(?i)inoculation|feeding|weighing|ultrasound|movement") String type, String description,
                   Date executionDate, @NotEmpty(message = "Cuig is required") String establishmentCuig,
                   String food, Long amount, String bovineTag, String workerUserName) {
        super(id, name, type, description, executionDate, establishmentCuig, workerUserName);
        this.food = food;
        this.amount = amount;
        this.bovineTag = bovineTag;
    }
}
