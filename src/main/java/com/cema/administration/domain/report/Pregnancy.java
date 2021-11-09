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
public class Pregnancy implements Reported<Pregnancy> {

        @ApiModelProperty(notes = "The year for this data", example = "2015")
        private Integer year;
        @ApiModelProperty(notes = "The percentage of pregnant cows", example = "72")
        private Float percentage;

        @Override
        public int compareTo(@NotNull Pregnancy o) {
                return this.year.compareTo(o.getYear());
        }
}
