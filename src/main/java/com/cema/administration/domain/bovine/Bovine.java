package com.cema.administration.domain.bovine;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bovine {
    @ApiModelProperty(notes = "The tag attached to this bovine", example = "123")
    @NotEmpty(message = "Tag is required")
    private String tag;
    @ApiModelProperty(notes = "The description for this bovine", example = "Brown with spots")
    private String description;
    @ApiModelProperty(notes = "The bovine genre", example = "macho")
    @NotEmpty(message = "genre is required")
    @Pattern(regexp = "(?i)macho|hembra")
    private String genre;
    @ApiModelProperty(notes = "The bovine category", example = "vaca")
    @Pattern(regexp = "(?i)vaca|toro|ternero")
    private String category;
    @ApiModelProperty(notes = "The bovine status", example = "destetado")
    @NotEmpty(message = "status is required")
    @Pattern(regexp = "(?i)mamando|destetado|preñada|sin preñez|en servicio|muerto|vendido|fuera de servicio")
    private String status;
    @ApiModelProperty(notes = "The cuig of the establishment this bovine belongs to", example = "321")
    @NotEmpty(message = "Establishment is required")
    private String establishmentCuig;
    @ApiModelProperty(notes = "Date when the bovine was tagged")
    private Date taggingDate;
    @ApiModelProperty(notes = "Date when the bovine was born")
    private Date birthDate;
    @ApiModelProperty(notes = "The batches this bovine belongs to", example = "[\"batch_1\",\"OtherBatch\",\"bigbatch\",\"Some_Batch\"]")
    private List<String> batchNames;

    public Integer getTaggingYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(taggingDate);
        return calendar.get(Calendar.YEAR);
    }

}
