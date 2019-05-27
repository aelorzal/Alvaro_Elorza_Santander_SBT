package com.Alvaro_Elorza_Santander_SBT.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.Alvaro_Elorza_Santander_SBT.model.Account;
import com.Alvaro_Elorza_Santander_SBT.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;

/**
* This controller manages the HTTP requests for accounts and implements the different operations available
*/
@RestController
@Slf4j
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	AccountRepository accountRepository;

	/**
	* This method retrieves the information of an account
	* @param id Account Id
	* @return Account details in responseEntity format
	* @throws Exception in case and error happens, this will mean that the user gets a HTTP 500 error code, in a real
	* world application this should be changed with a json with meaningful information
	*/
	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> getAccountById(@Valid @PathVariable Long id) throws Exception {

		log.info("account >> " + id);

		ResponseEntity<Account> responseEntity = new ResponseEntity<Account>(HttpStatus.OK);

		Optional<Account> optional = accountRepository.findById(id);
		if (optional.isPresent()) {
			responseEntity = new ResponseEntity<Account>(optional.get(), HttpStatus.OK);
		}

		return responseEntity;

	}

	
	/**
	 * This method provides a list of available accounts
	 * @return List Account in responseEntity format 
	 * @throws Exception in case and error happens, this will mean that the user gets a HTTP 500 error code, in a real
	 * world application this should be changed with a json with meaningful information
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Account>> getAllAccounts() throws Exception {

		log.info("getAllAccounts");
		List<Account> accountList = accountRepository.findAll();
		return new ResponseEntity<List<Account>>(accountList, HttpStatus.OK);

	}

	/**
	* This method allows you to modify an account with the values ​​provided
	* @param accountUpdate Account details to modify
	* @return ResponseEntity
	* @throws Exception in case and error happens, this will mean that the user gets a HTTP 500 error code, in a real
	* world application this should be changed with a json with meaningful information
	*/
	@RequestMapping(method = RequestMethod.PUT, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateAccount(@Valid @RequestBody Account accountUpdate) throws Exception {

		log.info("updateAccountById >>" + accountUpdate.getId());


		Optional<Account> optional = accountRepository.findById(accountUpdate.getId());
		if (optional.isPresent()) {
			accountRepository.save(accountUpdate);
		}

		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	/**
	 * This method allows you to delete an account by the id provided
	 * @param id Account Id
	 * @return ResponseEntity
	 * @throws Exception in case and error happens, this will mean that the user gets a HTTP 500 error code, in a real
	 * world application this should be changed with a json with meaningful information
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteAccountById(@Valid @PathVariable Long id) throws Exception {
		log.info("deleteAccountById >>" + id);

		Optional<Account> optional = accountRepository.findById(id);
		if (optional.isPresent()) {
			accountRepository.deleteById(id);
		}

		return new ResponseEntity<Void>(HttpStatus.OK);

	}
}
