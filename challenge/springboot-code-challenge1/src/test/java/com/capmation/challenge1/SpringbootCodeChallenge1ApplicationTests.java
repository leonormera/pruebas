package com.capmation.challenge1;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import net.minidev.json.JSONArray;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringbootCodeChallenge1ApplicationTests {
	
	@Autowired
    TestRestTemplate restTemplate;

	@Test
    @DirtiesContext
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("user1", "user1$$pwd")
                .getForEntity("/bankaccounts/1001", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1001);

        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(1000.00);
        
        String accountType = documentContext.read("$.accountType");
        assertThat(accountType).isEqualTo("SAVINGS");
        
        String owner = documentContext.read("$.owner");
        assertThat(owner).isEqualTo("user1");
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("user1", "user1$$pwd")
                .getForEntity("/bankaccounts/1009", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }
    
    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("user1", "user1$$pwd")
                .getForEntity("/bankaccounts", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(1001, 1002, 1003);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(1000.00, 10.00, 1500.00);
    }
    
    @Test
    void shouldCreateANewBankAccount() {
    	//TODO: Create a new bank account using user1 and validate that the new location created is available
    	BankAccount ba = new BankAccount(1001L, Double.valueOf(200), "SAVINGS", "LMA");
    	HttpHeaders ht = new HttpHeaders();
    	ht.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<BankAccount> he = new HttpEntity<>(ba, ht);
   	
    	ResponseEntity<Void> response = restTemplate
                .withBasicAuth("user1", "user1$$pwd")
                .postForEntity("/bankaccounts", he, Void.class);
    	
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED); 
        String loc = response.getHeaders().get("Location").get(0);
        assertThat(loc !=null);
        assertThat(loc.contains(String.valueOf(ba.id())));
        
    }
    
    @Test
    void shouldDepositMoneyInBankAccount() {
    	//allowMethods("PATCH");
    	//TODO: Do a normal deposit into any bank account and validate expected new account amount value
    	// 1.- Create Account
    	BankAccount ba = new BankAccount(1001L, Double.valueOf(200), "SAVINGS", "LMA");
    	HttpHeaders ht = new HttpHeaders();
    	ht.setContentType(MediaType.APPLICATION_JSON);
    	HttpEntity<BankAccount> request = new HttpEntity<>(ba, ht);

    	ResponseEntity<Void> response = restTemplate
                .withBasicAuth("user1", "user1$$pwd")
                .postForEntity("/bankaccounts", request, Void.class);
    	// 2.- Make a deposit
    	
    	Date fecha = Date.from(LocalDate.now().atStartOfDay()
    		      .atZone(ZoneId.systemDefault())
    		      .toInstant());
    	
    	DepositRecord dr = new DepositRecord(1002L,Double.valueOf(300),fecha);
    	
    	HttpEntity<DepositRecord> request2 = new HttpEntity<>(dr, ht);
    	
    	ResponseEntity<BankAccount> response2 = restTemplate.exchange("/bankaccounts/{requestedId}/deposit", HttpMethod.PATCH, request2, BankAccount.class,ba.id());
    	
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK); 
       
        assertThat(ba.amount()+dr.amount() == response2.getBody().amount());
    
    }
    
    @Test
    void shouldWithdrawMoneyFromBankAccount() {
    	//TODO: Do a normal withdrawal from one bank account and validate expected new account amount value

    }
    
    @Test
    void shouldTransferMoneyInBankAccount() {
    	//TODO: Do a normal deposit into any bank account and validate expected new account amount value
    }
    
    private static void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
