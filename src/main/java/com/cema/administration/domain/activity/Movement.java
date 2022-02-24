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
public class Movement extends Activity {

    @ApiModelProperty(notes = "The destination for this movement")
    @NotEmpty(message = "Location name is required")
    private String locationName;
    @ApiModelProperty(notes = "The tag this activity is associated to. Either this or batchName must be populated", example = "1234")
    private String bovineTag;
    @ApiModelProperty(notes = "The batch this activity is associated to. Either this or bovineTag must be populated", example = "the_batch")
    private String batchName;

    @Builder
    public Movement(UUID id, @NotEmpty(message = "Name is required") String name,
                    @NotEmpty(message = "Type is required") @Pattern(regexp = "(?i)inoculation|feeding|weighing|ultrasound|movement") String type,
                    String description, Date executionDate, @NotEmpty(message = "Cuig is required") String establishmentCuig,
                    String locationName, String bovineTag, String batchName, String workerUserName) {
        super(id, name, type, description, executionDate, establishmentCuig, workerUserName);
        this.locationName = locationName;
        this.bovineTag = bovineTag;
        this.batchName = batchName;
    }
}
