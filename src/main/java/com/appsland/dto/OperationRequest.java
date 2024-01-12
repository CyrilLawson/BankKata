package com.appsland.dto;

import javax.validation.constraints.NotNull;

/**
 *
 * DTO pour déposer ou retirer de l'argent sur un compte
 */
public record OperationRequest (@NotNull
                                Integer accountNumber,
                                @NotNull
                                Double operationAmount) { }
