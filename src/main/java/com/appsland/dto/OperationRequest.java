package com.appsland.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 *
 * DTO pour déposer ou retirer de l'argent sur un compte
 */
public record OperationRequest (@NotNull
                                @ApiModelProperty(value = "Account number")
                                Integer accountNumber,
                                @NotNull
                                @ApiModelProperty(value = "Operation amount")
                                Double operationAmount) { }
