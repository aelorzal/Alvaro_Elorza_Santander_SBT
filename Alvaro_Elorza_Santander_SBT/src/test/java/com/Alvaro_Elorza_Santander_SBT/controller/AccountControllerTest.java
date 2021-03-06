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



/**
* This controller manages the HTTP requests for accounts and implements the different operations available
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

	/**
	 * Try recovering an account for id not found
	 * @result Expect an HttpStatus 200 and an empty json. 
	 * @throws URISyntaxException
	 */
	@Test
	public void testNotFoundGetAccountByID() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/-1");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNull(responseEntity.getBody());

	}
	
	/**
	 * Try recovering an account for id with wrong type of id
	 * @result Expect an HttpStatus 400, in a real world application this should be completed with a json with error information
	 * @throws URISyntaxException
	 */
	@Test
	public void testWrongIdTypeGetAccountByID() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/dsa");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.GET, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

	}

	/**
	 * Try recovering an account for id
	 * @result Expect an HttpStatus 200 and a json with account details
	 * @throws URISyntaxException
	 */	
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

	/**
	 * Try recovering a list of Accounts
	 * @result Expect an HttpStatus 200 and a List of accounts.
	 * @throws URISyntaxException
	 */
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

	/**
	 * Try update an account not found
	 * @result Expect an HttpStatus 200, in a real world application this should be completed with a json with informning the number of updates, in this case 0
	 * @throws URISyntaxException
	 */
	@Test
	public void testNotFoundUpdateAccount() throws URISyntaxException {

		Account updateAccount = new Account();
		updateAccount.setId(-1L);
		updateAccount.setName("No Account");
		updateAccount.setBalance(19.99D);
		

		URI uri = new URI(getRootUrl() + "/accounts/");

		RequestEntity<Account> requestEntity = RequestEntity.put(uri).body(updateAccount);

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	}
	
	/**
	 * Try update an account with json param name key format incorrect
	 * @result Expect an HttpStatus 400, in a real world application this should be completed with a json with error information
	 * @throws URISyntaxException
	 */
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
	
	/**
	 * Try update an account with json param id value type incorrect
	 * @result Expect an HttpStatus 400, in a real world application this should be completed with a json with error information
	 * @throws URISyntaxException
	 */
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
	
	/**
	 * Try update an account with json param name size incorrect
	 * @result Expect an HttpStatus 400, in a real world application this should be completed with a json with error information
	 * @throws URISyntaxException
	 */
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
	
	/**
	 * Try update an account
	 * @result Expect an HttpStatus 200, in a real world application this should be completed with a json with informning the number of updates
	 * @throws URISyntaxException
	 */
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

	/**
	 * Try delete an account by id not found 
	 * @result Expect an HttpStatus 200, in a real world application this should be completed with a json with informning the number of deletes, in this case 0
	 * @throws URISyntaxException
	 */
	@Test
	public void testNotFoundDeleteAccountById() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/-1");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

		ResponseEntity<Account> responseEntity = restTemplate.exchange(requestEntity, Account.class);

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	}

	/**
	 * Try delete an account by id 
	 * @result Expect an HttpStatus 200, in a real world application this should be completed with a json with informning the number of deletes
	 * @throws URISyntaxException
	 */
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
	
	/**
	 * Try delete an account with id value type incorrect
	 * @result Expect an HttpStatus 400, in a real world application this should be completed with a json with error information
	 * @throws URISyntaxException
	 */
	@Test
	public void testWrongIdTypeDeleteAccountById() throws URISyntaxException {

		URI url = new URI(getRootUrl() + "/accounts/sas");

		RequestEntity<Void> requestEntity = RequestEntity.method(HttpMethod.DELETE, url).build();

		ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

	}

}
