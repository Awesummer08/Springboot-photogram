package com.cos.photogramstart.web;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.handler.ex.CustomValidationException;
import com.cos.photogramstart.service.AuthService;
import com.cos.photogramstart.web.dto.auth.SignupDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	
	@GetMapping("/auth/signin")
	public String signinForm() {
		return "auth/signin";
	}
	

	@GetMapping("/auth/signup")
	public String signupForm() {
		return "auth/signup";
	}
	
	@PostMapping("/auth/signup")
	public String signup(@Valid SignupDto signupDto, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
		
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("================");
				System.out.println(error.getDefaultMessage());
				System.out.println("================");
			}
				throw new CustomValidationException("유효성 검사 실패함", errorMap);
			 
		}else {
			User user = signupDto.toEntity();
			User entityUser = authService.회원가입(user);
			log.info(entityUser.toString());
			return "auth/signin";
		}

	}
	
}
