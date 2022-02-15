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
public class Live implements Reported<Live> {

    @ApiModelProperty(notes = "The year for this data", example = "2015")
    private Integer year;
    @ApiModelProperty(notes = "The live animals for this category", example = "300")
    private Integer count;
    @ApiModelProperty(notes = "The category of the animals", example = "Vaca")
    private String category;

    public void addOne() {
        count++;
    }


    @Override
    public int compareTo(@NotNull Live o) {
        return this.year.compareTo(o.getYear()) != 0 ?
                this.year.compareTo(o.getYear()) : this.getCategory().compareTo(o.getCategory());
    }
}
