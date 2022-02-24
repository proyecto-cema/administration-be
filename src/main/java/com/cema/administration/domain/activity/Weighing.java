package com.cema.administration.domain.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ToString(callSuper=true)
public class Weighing extends Activity {

    @ApiModelProperty(notes = "The weight measures in kilograms", example = "5000")
    private Long weight;
    @ApiModelProperty(notes = "The category of the animal", example = "toro")
    @Pattern(regexp = "(?i)ternero|vaca|toro")
    private String category;
    @ApiModelProperty(notes = "Status notes of the animal's teeth", example = "small teeth")
    private String dentalNotes;
    @ApiModelProperty(notes = "The tag this activity is associated to.", example = "1234")
    @NotEmpty(message = "Bovine Tag is required")
    private String bovineTag;

    @JsonIgnore
    public Long getWeightSafely() {
        return weight != null ? weight : 0L;
    }

    @Builder
    public Weighing(UUID id, @NotEmpty(message = "Name is required") String name,
                    @NotEmpty(message = "Type is required") @Pattern(regexp = "(?i)(?i)inoculation|feeding|weighing|ultrasound|movement") String type,
                    String description, Date executionDate,
                    @NotEmpty(message = "Cuig is required") String establishmentCuig,
                    Long weight, String category, String dentalNotes, String bovineTag, String workerUserName) {
        super(id, name, type, description, executionDate, establishmentCuig, workerUserName);
        this.weight = weight;
        this.category = category;
        this.dentalNotes = dentalNotes;
        this.bovineTag = bovineTag;
    }

}
