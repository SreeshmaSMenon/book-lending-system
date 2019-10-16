package com.hcl.booklendingsystem.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.booklendingsystem.dto.LoginRequest;
import com.hcl.booklendingsystem.dto.LoginResponse;
import com.hcl.booklendingsystem.entity.User;
import com.hcl.booklendingsystem.exception.BindException;
import com.hcl.booklendingsystem.exception.UserNotFoundException;
import com.hcl.booklendingsystem.service.LoginService;
import com.hcl.booklendingsystem.util.BookLendingSystemConstants;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@CrossOrigin(allowedHeaders = {"*","*/"}, origins = {"*","*/"})
public class LoginController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	LoginService loginService;
	/**
	 * @param loginRequest 
	 * @param bindingResult
	 * @return ResponseEntity of LoginResponse 
	 */
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
    	LOGGER.debug(BookLendingSystemConstants.LOGIN_DEBUG_END_CONTROLLER);
    	 LoginResponse loginResponse=new LoginResponse();
        if (bindingResult.hasErrors()) {
        	throw new BindException(bindingResult.getFieldError().getField()+" "+bindingResult.getFieldError().getDefaultMessage());
        }
        Optional<User> optionalUser=loginService.getUerByUsernameAndPassword(loginRequest);
        if(optionalUser.isPresent()) {
        	loginResponse.setStatusCode(HttpStatus.OK.value());
        	loginResponse.setMessage(BookLendingSystemConstants.SUCCESS);
        	loginResponse.setUserId(optionalUser.get().getUserId());
        }else {
        	throw new UserNotFoundException(BookLendingSystemConstants.USER_NOT_FOUND);
        }
        LOGGER.debug(BookLendingSystemConstants.LOGIN_DEBUG_START_CONTROLLER);
        return new ResponseEntity<>(loginResponse,HttpStatus.CREATED);
    }
}
