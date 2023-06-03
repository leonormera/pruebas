package com.capmation.challenge1;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class SpringbootCodeChallenge1JsonTests {
	@Autowired
	private JacksonTester<BankAccount> json;
	
	@Autowired
	private JacksonTester<BankAccount[]> jsonList;
	
	private BankAccount[] bankAccounts;
	
	@BeforeEach
    void setUp() {
        bankAccounts = Arrays.array(
                new BankAccount(1001L, 1000.00, "SAVINGS", "user1"),
                new BankAccount(1002L, 10.00, "CHECKING", "user1"),
                new BankAccount(1003L, 1500.00, "SAVINGS", "user1"),
        		new BankAccount(1004L, 4700.50, "SAVINGS", "user2"));
    }

    @Test
    public void BankAccountSerializationTest() throws IOException {
        BankAccount bankAccount = bankAccounts[0];
        assertThat(json.write(bankAccount)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(bankAccount)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(bankAccount)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(1001);
        assertThat(json.write(bankAccount)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(bankAccount)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(1000.00);
        assertThat(json.write(bankAccount)).hasJsonPathStringValue("@.accountType");
        assertThat(json.write(bankAccount)).extractingJsonPathStringValue("@.accountType")
                .isEqualTo("SAVINGS");
        assertThat(json.write(bankAccount)).hasJsonPathStringValue("@.owner");
        assertThat(json.write(bankAccount)).extractingJsonPathStringValue("@.owner")
                .isEqualTo("user1");
    }

    @Test
    public void BankAccountDeserializationTest() throws IOException {
        String expected = """
                {
				  "id": 1001,
				  "amount": 1000.00, 
				  "accountType": "SAVINGS", 
				  "owner": "user1"
				}
                """;
        assertThat(json.parse(expected))
                .isEqualTo(new BankAccount(1001L, 1000.00, "SAVINGS", "user1"));
        assertThat(json.parseObject(expected).id()).isEqualTo(1001L);
        assertThat(json.parseObject(expected).amount()).isEqualTo(1000.00);
        assertThat(json.parseObject(expected).accountType()).isEqualTo("SAVINGS");
        assertThat(json.parseObject(expected).owner()).isEqualTo("user1");
    }

    @Test
    void BankAccountListSerializationTest() throws IOException {
        assertThat(jsonList.write(bankAccounts)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void BankAccountListDeserializationTest() throws IOException {
        String expected = """
                [
                     {"id": 1001, "amount": 1000.00, "accountType": "SAVINGS", "owner": "user1"},
					  {"id": 1002, "amount": 10.00, "accountType": "CHECKING", "owner": "user1"},
					  {"id": 1003, "amount": 1500.00, "accountType": "SAVINGS", "owner": "user1" },
					  {"id": 1004, "amount": 4700.50, "accountType": "SAVINGS", "owner": "user2" }
                                                  
                ]
                """;
        assertThat(jsonList.parse(expected)).isEqualTo(bankAccounts);
    }
}
