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
public class Disease implements Reported<Disease> {

    @ApiModelProperty(notes = "The year for this data", example = "2015")
    private Integer year;
    @ApiModelProperty(notes = "The number of infections if this disease", example = "72")
    private Integer infections;
    @ApiModelProperty(notes = "The name of the disease", example = "Aftosa")
    private String name;

    @Override
    public int compareTo(@NotNull Disease o) {
        return this.year.compareTo(o.getYear()) != 0 ?
                this.year.compareTo(o.getYear()) : this.getName().compareTo(o.getName());
    }
}
