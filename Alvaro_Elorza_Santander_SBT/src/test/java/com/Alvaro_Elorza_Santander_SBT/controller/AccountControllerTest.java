package com.Alvaro_Elorza_Santander_SBT.controller;

import static org.hamcrest.CoreMatchers.containsString;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.Alvaro_Elorza_Santander_SBT.model.Account;
import com.Alvaro_Elorza_Santander_SBT.repository.AccountRepository;

/*
Instead of testing server classes unitarily, I preferred to write them with TDD aligned to the user stories, which have a client perspective.
*/

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@ActiveProfiles("test")
public class AccountControllerTest {

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
	public void testNotFoundGetAccountByID() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/-1");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

	}
	
	@Test
	public void testWrongIdTypeGetAccountByID() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/dsa");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

	}

	@Test
	public void testFoundGetAccountByID() throws URISyntaxException {
		Account getAccount = accountRepository.findAll().get(0);

		URI url = new URI(getRootUrl() + "/accounts/" + getAccount.getId());

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Account account = responseEntity.getBody();
		Assert.assertNotNull(account);
		Assert.assertEquals(getAccount.getId(), account.getId());
		Assert.assertEquals(getAccount.getName(), account.getName());
		Assert.assertEquals(getAccount.getBalance(), account.getBalance());

		accountRepository.delete(getAccount);
	}

	@Test
	public void testGetAllAccounts() throws URISyntaxException {

		int accountsSize = accountRepository.findAll().size();

		URI url = new URI(getRootUrl() + "/accounts/");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

		ResponseEntity<List<Account>> responseEntity = restTemplate.exchange(requestEntity,
				new ParameterizedTypeReference<List<Account>>() {
				});

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		List<Account> accountList = responseEntity.getBody();
		Assert.assertNotNull(accountList);
		Assert.assertEquals(accountList.size(), accountsSize);
	}

	@Test
	public void testNotFoundUpdateAccount() throws URISyntaxException {

		Account updateAccount = new Account();
		updateAccount.setId(-1L);
		updateAccount.setName("No Account");
		updateAccount.setBalance(19.99D);
		

		URI uri = new URI(getRootUrl() + "/accounts/");

		RequestEntity<Account> requestEntity = RequestEntity.put(uri).body(updateAccount);

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

	}

	@Test
	public void testJsonKeyFormatUpdateAccount() throws URISyntaxException, JSONException {


		/* Test json keys are correct spelled*/
		JSONObject updateAccount = new JSONObject();
		updateAccount.put("id", 1);
		updateAccount.put("names", "Test Format");
		updateAccount.put("balance", 564);

		URI uri = new URI(getRootUrl() + "/accounts/");
	
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> request = new HttpEntity<String>(updateAccount.toString(), headers);
	     
	    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertThat(responseEntity.getBody().toString(), containsString("Name_Not_Null"));

		

	}
	
	@Test
	public void testJsonValueTypeUpdateAccount() throws URISyntaxException, JSONException {



		/* Test json values are correct type*/
		JSONObject updateAccount = new JSONObject();
		updateAccount.put("id", "");
		updateAccount.put("name", "Test Format");
		updateAccount.put("balance", 564);

		URI uri = new URI(getRootUrl() + "/accounts/");
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<String>(updateAccount.toString(), headers);
	     
	    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);


		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertThat(responseEntity.getBody().toString(), containsString("Id_Not_Null"));
		

	}
	
	@Test
	public void testJsonValueLengthUpdateAccount() throws URISyntaxException, JSONException {



		/* Test json values are correct type*/
		JSONObject updateAccount = new JSONObject();
		updateAccount.put("id", 1);
		updateAccount.put("name", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries,");
		updateAccount.put("balance", 564);

		URI uri = new URI(getRootUrl() + "/accounts/");
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<String>(updateAccount.toString(), headers);
	     
	    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);


		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertThat(responseEntity.getBody().toString(), containsString("Name_Size_Error"));
		
		

	}
	
	@Test
	public void testFoundUpdateAccount() throws URISyntaxException {

		Account updateAccount = accountRepository.findAll().get(0);
		updateAccount.setName("Account updated");
		updateAccount.setBalance(updateAccount.getBalance() + 25.15D);

		URI uri = new URI(getRootUrl() + "/accounts/");

		RequestEntity<Account> requestEntity = RequestEntity.put(uri).body(updateAccount);

		ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		Optional<Account> optional = accountRepository.findById(updateAccount.getId());
		Assert.assertTrue(optional.isPresent());
		Account account = optional.get();
		Assert.assertEquals(updateAccount.getId(), account.getId());
		Assert.assertEquals(updateAccount.getName(), account.getName());
		Assert.assertEquals(updateAccount.getBalance(), account.getBalance());

	}

	@Test
	public void testNotFoundDeleteAccountById() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/-1");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		// Ensure Status Code is 404 Not Found
		Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

	}

	@Test
	public void testFoundDeleteAccountById() throws URISyntaxException {

		Account deleteAccount = accountRepository.findAll().get(0);
		URI url = new URI(getRootUrl() + "/accounts/" + deleteAccount.getId());

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

		ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Optional<Account> optional = accountRepository.findById(deleteAccount.getId());
		Assert.assertFalse(optional.isPresent());

	}
	
	@Test
	public void testWrongIdTypeDeleteAccountById() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/sas");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

		ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

	}

}
