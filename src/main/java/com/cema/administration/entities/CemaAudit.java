package com.cema.administration.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cema_audit")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CemaAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "local_address")
    private String localAddress;

    @Column(name = "request_headers")
    private String requestHeaders;

    @Column(name = "uri")
    private String uri;

    @Column(name = "response_status")
    private String responseStatus;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "establishment_cuig")
    private String establishmentCuig;

    @Column(name = "method")
    private String method;

    @Column(name = "role")
    private String role;

    @Column(name = "requestor_username")
    private String requestorUsername;

    @Column(name = "audit_date")
    private Date auditDate;

    @Column(name = "module")
    private String module;
}
