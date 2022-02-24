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
public class FoodConsumption implements Reported<FoodConsumption> {

    @ApiModelProperty(notes = "The year for this data", example = "2015")
    private Integer year;
    @ApiModelProperty(notes = "The average amount of food eaten by this category in kilograms", example = "7000")
    private Long foodEaten;
    @ApiModelProperty(notes = "The category of the measured animal", example = "Vaca")
    private String category;

    @Override
    public int compareTo(@NotNull FoodConsumption o) {
        return this.year.compareTo(o.getYear()) != 0 ?
                this.year.compareTo(o.getYear()) : this.getCategory().compareTo(o.getCategory());
    }
}
