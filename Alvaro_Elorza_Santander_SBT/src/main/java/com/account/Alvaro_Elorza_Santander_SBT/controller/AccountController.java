package com.account.Alvaro_Elorza_Santander_SBT.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.account.Alvaro_Elorza_Santander_SBT.model.Account;
import com.account.Alvaro_Elorza_Santander_SBT.repository.AccountRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Account Management System")
public class AccountController {

	protected static Logger logger = LogManager.getLogger(AccountController.class);

	@Autowired
	AccountRepository accountRepository;

	@ApiOperation(value = "get the account by id", response = Account.class)
	@RequestMapping(method = RequestMethod.GET, value = "/getAccountById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Account> getAccountById(@PathVariable Long id) {

		logger.info("getAccountById >> " + id);
		ResponseEntity<Account> responseEntity = new ResponseEntity<Account>(HttpStatus.NOT_FOUND);

		try {
			Optional<Account> optional = accountRepository.findById(id);
			if (optional.isPresent()) {
				responseEntity = new ResponseEntity<Account>(optional.get(), HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseEntity;

	}

	@ApiOperation(value = "View a list of available accounts", response = List.class)
	@RequestMapping(method = RequestMethod.GET, value = "/getAllAccounts/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Account>> getAllAccounts() {

		logger.info("getAllAccounts");
		ResponseEntity<List<Account>> responseEntity = new ResponseEntity<List<Account>>(HttpStatus.NOT_FOUND);
		try {
			List<Account> accountList = accountRepository.findAll();
			responseEntity = new ResponseEntity<List<Account>>(accountList, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseEntity;

	}

	@ApiOperation(value = "Update de given account", response = ResponseEntity.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/updateAccountById/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateAccountById(@RequestBody Account accountUpdate) {

		logger.info("updateAccountById >>" + accountUpdate.getId());

		HttpStatus httpStatus = HttpStatus.NOT_FOUND;

		try {
			Optional<Account> optional = accountRepository.findById(accountUpdate.getId());
			if (optional.isPresent()) {
				accountRepository.save(accountUpdate);
				httpStatus = HttpStatus.OK;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return new ResponseEntity<Void>(httpStatus);

	}

	@ApiOperation(value = "Delete the account by id", response = ResponseEntity.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/deleteAccountById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteAccountById(@PathVariable Long id) {

		ResponseEntity<Void> responseEntity = new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

		try {
			Optional<Account> optional = accountRepository.findById(id);
			if (optional.isPresent()) {
				accountRepository.deleteById(id);
				responseEntity = new ResponseEntity<Void>(HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseEntity;

	}
}
