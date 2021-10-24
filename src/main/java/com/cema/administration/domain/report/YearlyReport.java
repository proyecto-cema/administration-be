package com.cema.administration.domain.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YearlyReport {

    @ApiModelProperty(notes = "The type of report", example = "Disease")
    private String type;
    @ApiModelProperty(notes = "A brief description of the report meaning")
    private String description;

    @Singular("reported")
    private List<Reported> reportedList;

    public void filterByYear(Integer yearFrom, Integer yearTo) {
        if (yearFrom != -1) {
            filterByYearFrom(yearFrom);
        }
        if (yearTo != -1) {
            filterByYearTo(yearTo);
        }
    }

    public void filterByYearFrom(Integer yearFrom) {
        reportedList = reportedList.stream()
                .filter(reported -> reported.getYear() >= yearFrom)
                .collect(Collectors.toList());
    }

    public void filterByYearTo(Integer yearTo) {
        reportedList = reportedList.stream()
                .filter(reported -> reported.getYear() <= yearTo)
                .collect(Collectors.toList());
    }
}
