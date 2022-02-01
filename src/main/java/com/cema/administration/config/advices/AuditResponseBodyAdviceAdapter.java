package com.cema.administration.config.advices;

import com.cema.administration.domain.CemaUserDetails;
import com.cema.administration.entities.CemaAudit;
import com.cema.administration.repositories.AuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@ControllerAdvice
@Slf4j
public class AuditResponseBodyAdviceAdapter implements ResponseBodyAdvice<Object> {

    private final AuditRepository auditRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public AuditResponseBodyAdviceAdapter(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String originMethod = returnType.getMethod().getName();
        if (originMethod.contains("Audit")) {
            return body;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CemaUserDetails)) {
            return body;
        }
        if (request instanceof ServletServerHttpRequest &&
                response instanceof ServletServerHttpResponse) {
            CemaAudit cemaAudit = new CemaAudit();

            try {
                Field field = returnType.getClass().getDeclaredField("returnValue");
                field.setAccessible(true);
                ResponseEntity value = (ResponseEntity) field.get(returnType);
                cemaAudit.setResponseStatus(String.valueOf(value.getStatusCode()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Unable to recover response status.");
            }

            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            String input = "Unable to recover.";
            if (serverHttpRequest.getServletRequest() instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) serverHttpRequest.getServletRequest();
                try {
                    input = ByteSource.wrap(requestWrapper.getContentAsByteArray())
                            .asCharSource(StandardCharsets.UTF_8).read();
                } catch (IOException e) {
                    log.error("Unable to recover request body.");
                }
            } else {
                log.info("Cannot recover request body, request of wrong class {}", serverHttpRequest.getServletRequest().getClass());
            }

            cemaAudit.setRequestBody(input);
            try {
                cemaAudit.setResponseBody(mapper.writeValueAsString(body));
            } catch (JsonProcessingException e) {
                log.error("Unable to parse response body.");
            }
            cemaAudit.setLocalAddress(String.valueOf(request.getLocalAddress()));
            cemaAudit.setRequestHeaders(String.valueOf(request.getHeaders()));
            cemaAudit.setUri(String.valueOf(request.getURI()));
            cemaAudit.setHttpMethod(request.getMethodValue());
            cemaAudit.setMethod(String.valueOf(returnType.getMethod()));
            cemaAudit.setRole(String.valueOf(authentication.getAuthorities()));
            cemaAudit.setAuditDate(new Date());
            cemaAudit.setModule("administration");

            CemaUserDetails cemaUserDetails = (CemaUserDetails) authentication.getPrincipal();
            cemaAudit.setRequestorUsername(cemaUserDetails.getUsername());
            cemaAudit.setEstablishmentCuig(cemaUserDetails.getCuig());


            auditRepository.save(cemaAudit);
        }
        return body;
    }
}
