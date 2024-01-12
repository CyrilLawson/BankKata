package com.appsland.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * Objet réponse pour une opération réussie
 */
public record OperationResponse (String operationMessage,
                                @JsonInclude(JsonInclude.Include.NON_NULL) Object body) { }
