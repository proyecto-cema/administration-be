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
public class LiveCost implements Reported<LiveCost> {

    @ApiModelProperty(notes = "The year for this data", example = "2015")
    private Integer year;
    @ApiModelProperty(notes = "Kilogramos vivos", example = "1500.5")
    private Long liveWeight;
    @ApiModelProperty(notes = "Gastos para el periodo", example = "1123.5")
    private Long spending;
    @ApiModelProperty(notes = "Costo del kilogramo vivo", example = "1123.5")
    private Double cost;

    @Override
    public int compareTo(@NotNull LiveCost o) {
        return this.year.compareTo(o.getYear());
    }
}
