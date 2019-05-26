package com.account.Alvaro_Elorza_Santander_SBT.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.account.Alvaro_Elorza_Santander_SBT.model.Account;
import com.account.Alvaro_Elorza_Santander_SBT.repository.AccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountControllerTest {

	protected static Logger logger = LogManager.getLogger(AccountControllerTest.class);

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Value("${server.url}")
	private String serverUrl;

	private String getRootUrl() {
		return serverUrl + port;
	}

	@Test
	public void testNotFoundGetAccountByID() {

		try {

			URI url = new URI(getRootUrl() + "/getAccountById/2000");

			logger.info("testNotFoundGetAccountByID >> " + url.getPath());

			RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

			ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

			Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void testFoundGetAccountByID() throws URISyntaxException {
		try {
			Account getAccount = accountRepository.findAll().get(0);

			URI url = new URI(getRootUrl() + "/getAccountById/" + getAccount.getId());

			RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

			ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

			Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
			Account account = responseEntity.getBody();
			Assert.assertNotNull(account);
			Assert.assertEquals(getAccount.getId(), account.getId());
			Assert.assertEquals(getAccount.getName(), account.getName());
			Assert.assertEquals(getAccount.getBalance(), account.getBalance());

			accountRepository.delete(getAccount);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void testGetAllAccounts() {
		try {

			int accountsSize = accountRepository.findAll().size();

			URI url = new URI(getRootUrl() + "/getAllAccounts/");

			RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

			ResponseEntity<List<Account>> responseEntity = restTemplate.exchange(requestEntity,
					new ParameterizedTypeReference<List<Account>>() {
					});

			Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
			List<Account> accountList = responseEntity.getBody();
			Assert.assertNotNull(accountList);
			Assert.assertEquals(accountList.size(), accountsSize);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void testNotFoundUpdateAccountById() {
		try {
			Account updateAccount = new Account(0L, "No Account", 19.99D);

			URI uri = new URI(getRootUrl() + "/updateAccountById/");

			RequestEntity<Account> requestEntity = RequestEntity.put(uri).body(updateAccount);

			ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

			Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Test
	public void testFoundUpdateAccountByID() {
		try {

			Account updateAccount = accountRepository.findAll().get(0);
			updateAccount.setName("Account updated");
			updateAccount.setBalance(updateAccount.getBalance() + 25.15D);

			URI uri = new URI(getRootUrl() + "/updateAccountById/");

			RequestEntity<Account> requestEntity = RequestEntity.put(uri).body(updateAccount);

			ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

			Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

			Optional<Account> optional = accountRepository.findById(updateAccount.getId());
			Assert.assertTrue(optional.isPresent());
			Account account = optional.get();
			Assert.assertEquals(updateAccount.getId(), account.getId());
			Assert.assertEquals(updateAccount.getName(), account.getName());
			Assert.assertEquals(updateAccount.getBalance(), account.getBalance());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void testNotFoundDeleteAccountById() {
		try {
			URI url = new URI(getRootUrl() + "/deleteAccountById/0");

			RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

			ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

			// Ensure Status Code is 404 Not Found
			Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Test
	public void testFoundDeleteAccountById() {
		try {

			Account deleteAccount = accountRepository.findAll().get(0);
			URI url = new URI(getRootUrl() + "/deleteAccountById/" + deleteAccount.getId());

			RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

			ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

			Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
			Optional<Account> optional = accountRepository.findById(deleteAccount.getId());
			Assert.assertFalse(optional.isPresent());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
