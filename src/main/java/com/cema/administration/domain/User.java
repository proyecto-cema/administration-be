package com.cema.administration.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @ApiModelProperty(notes = "The username for this user, needed for login", example = "merlinds")
    private String userName;
    @ApiModelProperty(notes = "The name of this user", example = "Merlin")
    private String name;
    @ApiModelProperty(notes = "The last name of this user", example = "Nuñez")
    private String lastName;
    @ApiModelProperty(notes = "The phone number of this user", example = "3541330188")
    private String phone;
    @ApiModelProperty(notes = "The email address of this user", example = "merlinsn@gmail.com")
    private String email;
    @ApiModelProperty(notes = "The role of this user", example = "admin")
    private String role;
    @ApiModelProperty(notes = "When was this user created", hidden = true)
    private Date creationDate;
}
