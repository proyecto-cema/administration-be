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
public class Income implements Reported<Income> {

    @ApiModelProperty(notes = "The year for this data", example = "2015")
    private Integer year;
    @ApiModelProperty(notes = "Ingresos para el periodo", example = "1500.5")
    private Double earnings;
    @ApiModelProperty(notes = "Gastos para el periodo", example = "1123.5")
    private Double spending;

    @Override
    public int compareTo(@NotNull Income o) {
        return this.year.compareTo(o.getYear());
    }
}
