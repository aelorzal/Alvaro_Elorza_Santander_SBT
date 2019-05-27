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

@RestController
@Slf4j
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> getAccountById(@Valid @PathVariable Long id) {

		log.info("account >> " + id);
		
		ResponseEntity<Account> responseEntity = new ResponseEntity<Account>(HttpStatus.NOT_FOUND);

		try {
			Optional<Account> optional = accountRepository.findById(id);
			if (optional.isPresent()) {
				responseEntity = new ResponseEntity<Account>(optional.get(), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			responseEntity = new ResponseEntity<Account>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return responseEntity;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Account>> getAllAccounts() {

		log.info("getAllAccounts");
		ResponseEntity<List<Account>> responseEntity = new ResponseEntity<List<Account>>(HttpStatus.NOT_FOUND);
		try {
			List<Account> accountList = accountRepository.findAll();
			responseEntity = new ResponseEntity<List<Account>>(accountList, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return responseEntity;

	}

	@RequestMapping(method = RequestMethod.PUT, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateAccount(@Valid @RequestBody Account accountUpdate) {

		log.info("updateAccountById >>" + accountUpdate.getId());
		
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;

		try {
			Optional<Account> optional = accountRepository.findById(accountUpdate.getId());
			if (optional.isPresent()) {
				accountRepository.save(accountUpdate);
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<Void>(httpStatus);

	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteAccountById(@Valid @PathVariable Long id) {
		log.info("deleteAccountById >>" + id);

		ResponseEntity<Void> responseEntity = new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		try {
			Optional<Account> optional = accountRepository.findById(id);
			if (optional.isPresent()) {
				accountRepository.deleteById(id);
				responseEntity = new ResponseEntity<Void>(HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return responseEntity;

	}
}
