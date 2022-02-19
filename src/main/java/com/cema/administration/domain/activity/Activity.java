package com.cema.administration.domain.activity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Activity implements Comparable<Activity> {
    @ApiModelProperty(notes = "The id to identify this activity", example = "b000bba4-229e-4b59-8548-1c26508e459c")
    private UUID id;
    @ApiModelProperty(notes = "The name of this activity", example = "Actividad 2021")
    @NotEmpty(message = "Name is required")
    private String name;
    @ApiModelProperty(notes = "The type of activity", example = "Inoculation|Feeding|Weighing|Ultrasound|Movement")
    @NotEmpty(message = "Type is required")
    @Pattern(regexp = "(?i)inoculation|feeding|weighing|ultrasound|movement")
    private String type;
    @ApiModelProperty(notes = "The description of this activity", example = "Actividad realizada en invierno.")
    private String description;
    @ApiModelProperty(notes = "The date when this activity was executed")
    private Date executionDate;
    @ApiModelProperty(notes = "The cuig of the establishment this activity belongs to.", example = "321")
    @NotEmpty(message = "Cuig is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "The user name of the person who is going to complete this activity", example = "merlinds")
    private String workerUserName;

    public int getExecutionYear(){
        return executionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getYear();
    }


    @Override
    public int compareTo(@NotNull Activity o) {
        return o.getExecutionDate().compareTo(this.getExecutionDate());
    }
}
