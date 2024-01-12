package com.appsland.api;

import com.appsland.domain.Account;
import com.appsland.domain.Customer;
import com.appsland.domain.Operation;
import com.appsland.domain.OperationType;
import com.appsland.exception.AccountNotFoundException;
import com.appsland.exception.InsufficientBalanceException;
import com.appsland.repository.AccountRepository;
import com.appsland.repository.OperationRepository;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvcBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(value = SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OperationControllerShould {

    @Autowired
    private MockMvcBuilder mockMvcBuilder;
    @MockBean
    private AccountRepository mockAccountRepository;
    @MockBean
    private OperationRepository mockOperationRepository;

    private Customer customer;

    @Before
    @SneakyThrows
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(mockMvcBuilder);

         customer = Customer.builder()
                .email("test@gmail.com").age(12).firstName("test").lastName("test").build();

    }

    @Test
    public void throw_Exception_When_Account_NotFound() throws Exception {

        doReturn(Optional.empty()).when(mockAccountRepository).findByAccountNumber(any());

        mockMvcBuilder.build().perform(post("/v1/operations?operationType=WITHDRAWAL")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"accountNumber\": 1001, \"operationAmount\": 50}"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccountNotFoundException))
                .andExpect(result -> assertEquals("Account with number 1001 not found",
                        result.getResolvedException().getMessage()));

    }

    @Test
    public void throw_Exception_When_Operation_Amount_IsLess_OrEqual_ToZero() throws Exception {

        doReturn(Optional.of(Account.builder().accountNumber(1001).customer(customer).build()))
                .when(mockAccountRepository).findByAccountNumber(any());

        mockMvcBuilder.build().perform(post("/v1/operations?operationType=WITHDRAWAL")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"accountNumber\": 1001, \"operationAmount\": 0}"))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Amount must > 0", result.getResolvedException().getMessage()));

    }

    @Test
    public void throwExceptionWhenWithrawalAmountGreaterThanAccountBalance() throws Exception {

        doReturn(Optional.of(Account.builder().accountNumber(1001).customer(customer).build()))
                .when(mockAccountRepository).findByAccountNumber(any());

        mockMvcBuilder.build().perform(post("/v1/operations?operationType=WITHDRAWAL")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"accountNumber\": 1001, \"operationAmount\": 300}"))
                .andExpect(status().isNotAcceptable())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InsufficientBalanceException))
                .andExpect(result -> assertEquals("Insufficient balance !!", result.getResolvedException().getMessage()));

    }

    @Test
    public void returnSuccesfullDepositOperation() {

        doReturn(Optional.of(Account.builder().accountNumber(2001).customer(customer).build()))
                .when(mockAccountRepository).findByAccountNumber(any());

        // given:
        MockMvcRequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body("{ \"accountNumber\": 2001, \"operationAmount\": 50}");

        //when
        ResponseOptions response = given().spec(request)
                .post("/v1/operations?operationType=DEPOSIT");

        //then
        assertThat(response.statusCode()).isEqualTo(201);
        DocumentContext jsonBody = JsonPath.parse(response.getBody().asString());
        JSONObject jsonObject = new JSONObject((Map) jsonBody.json());
        assertThat(jsonObject).hasToString("{\"operationMessage\":\"Operation (DEPOSIT) of 50.0 on account 2001\",\"body\":2001}");
    }

    @Test
    public void returnSuccesfullWithrawalOperation() {

        Account account = Account.builder().accountNumber(2001).customer(customer).build();

        doReturn(Optional.of(account)).when(mockAccountRepository).findByAccountNumber(any());

        doReturn(Collections.singletonList(Operation.builder().operationType(OperationType.DEPOSIT)
                .account(account).amount(500d).build()))
                .when(mockOperationRepository).findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(any(LocalDate.class),
                eq(OperationType.DEPOSIT), eq(2001));

        // given:
        MockMvcRequestSpecification request = given()
                .header("Content-Type", "application/json")
                .body("{ \"accountNumber\": 2001, \"operationAmount\": 100}");

        //When
        ResponseOptions response = given().spec(request)
                .post("/v1/operations?operationType=WITHDRAWAL");

        // then:
        assertThat(response.statusCode()).isEqualTo(201);
        DocumentContext jsonBody = JsonPath.parse(response.getBody().asString());
        JSONObject jsonObject = new JSONObject((LinkedHashMap)jsonBody.json());
        assertThat(jsonObject).hasToString("{\"operationMessage\":\"Operation (WITHDRAWAL) of 100.0 on account 2001\",\"body\":2001}");
    }

    @Test
    public void returnAllOperations() throws JSONException {

        Account account = Account.builder().accountNumber(2001).customer(customer).build();

        doReturn(Optional.of(account)).when(mockAccountRepository).findByAccountNumber(any());

        doReturn(Arrays.asList(Operation.builder().operationType(OperationType.DEPOSIT)
                        .account(account).amount(500d).build(),
                                Operation.builder().operationType(OperationType.DEPOSIT)
                        .account(account).amount(100d).build()))
                .when(mockOperationRepository).findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(any(LocalDate.class),
                eq(OperationType.DEPOSIT), eq(2001));

        doReturn(Collections.singletonList(Operation.builder().operationType(OperationType.WITHDRAWAL)
                        .account(account).amount(300d).build()))
                .when(mockOperationRepository).findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(any(LocalDate.class),
                eq(OperationType.WITHDRAWAL), eq(2001));

        Operation operationDeposit1 = Operation.builder().operationType(OperationType.DEPOSIT)
                .date(LocalDate.now()).account(account).amount(500d).build();
        Operation operationDeposit2= Operation.builder().operationType(OperationType.DEPOSIT)
                .date(LocalDate.now()).account(account).amount(100d).build();
        Operation operationWithdrawal = Operation.builder().operationType(OperationType.WITHDRAWAL)
                .date(LocalDate.now()).account(account).amount(300d).build();

        doReturn(Arrays.asList(operationDeposit1, operationDeposit2, operationWithdrawal))
                .when(mockOperationRepository).findAllByDateLessThanEqualAndAccount_AccountNumber(any(LocalDate.class),
                        eq(2001), any(Pageable.class));

        ResponseOptions response = given().get("/v1/operations/2001?page=0&size=10");

        // then:
        assertThat(response.statusCode()).isEqualTo(200);
        DocumentContext jsonBody = JsonPath.parse(response.getBody().asString());
        JSONObject jsonObject = new JSONObject((LinkedHashMap)jsonBody.json());
        assertThat(jsonObject.get("operationMessage")).hasToString("Balance on " + LocalDate.now() + " = 300.0 for accountNumber : 2001");
        assertThat(jsonObject.get("body")).hasToString("[" +
                "{\"date\":\"" + LocalDate.now() + "\",\"amount\":500,\"operationType\":\"DEPOSIT\"}," +
                "{\"date\":\"" + LocalDate.now() + "\",\"amount\":100,\"operationType\":\"DEPOSIT\"}," +
                "{\"date\":\"" + LocalDate.now() + "\",\"amount\":300,\"operationType\":\"WITHDRAWAL\"}" +
                "]");
    }

}
