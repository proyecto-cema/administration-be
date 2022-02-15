package com.cema.administration.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Subscription {
    @ApiModelProperty(notes = "The date when this subscription was created")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startingDate;
    @ApiModelProperty(notes = "The date when this subscription was ended")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endingDate;
    @ApiModelProperty(notes = "The type of subscription")
    private SubscriptionType subscriptionType;
}
