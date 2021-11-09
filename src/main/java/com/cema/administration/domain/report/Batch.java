package com.cema.administration.domain.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batch implements Reported<Batch> {

    @ApiModelProperty(notes = "The year for this data", example = "2015")
    private Integer year;
    @ApiModelProperty(notes = "The average weight of the batch", example = "300")
    private Integer weight;
    @ApiModelProperty(notes = "The name for the batch", example = "vacas_negras")
    private String batchName;


    @Override
    public int compareTo(@NotNull Batch o) {
        return this.year.compareTo(o.getYear()) != 0 ?
                this.year.compareTo(o.getYear()) : this.getBatchName().compareTo(o.getBatchName());
    }
}
