package com.cos.photogramstart.service;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cos.photogramstart.domain.subscribe.SubscribeReposiotry;
import com.cos.photogramstart.domain.user.User;
import com.cos.photogramstart.domain.user.UserRepository;
import com.cos.photogramstart.handler.ex.CustomApiException;
import com.cos.photogramstart.handler.ex.CustomException;
import com.cos.photogramstart.handler.ex.CustomValidationApiException;
import com.cos.photogramstart.web.dto.user.UserProfileDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final SubscribeReposiotry subscribeReposiotry;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Value("${file.path}")
	private String uploadFolder;
	
	@Transactional
	public User 회원프로필사진변경(int principalId, MultipartFile profileImageFile) {
		UUID uuid =UUID.randomUUID();
		String imageFileName = uuid + "_" + profileImageFile.getOriginalFilename();
		
		Path imageFilePath = Paths.get(uploadFolder+imageFileName);
		
		try {
			Files.write(imageFilePath, profileImageFile.getBytes()); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		User userEntity = userRepository.findById(principalId).orElseThrow(()->{
			throw new CustomApiException(imageFileName);
		});
		
		userEntity.setProfileImageUrl(imageFileName);
		
		return userEntity;
	}
	
	
	@Transactional(readOnly = true)
	public UserProfileDto 회원프로필(int pageUserId, int principalId) {
		UserProfileDto dto = new UserProfileDto();
		
		//SELECT FROM image WHERE userId = :userId;
		User userEntity = userRepository.findById(pageUserId).orElseThrow(()->{
			throw new CustomException("해당 프로필 페이지는 없는 페이지입니다.");
		});  
		
		dto.setUser(userEntity);
		dto.setPageOwnerState(pageUserId == principalId);
		dto.setImageCount(userEntity.getImages().size()); 
		 
		int subscribeState = subscribeReposiotry.mSubscribeState(principalId, pageUserId);
		int subscribeCount = subscribeReposiotry.mSubscribeCount(pageUserId);
		
		dto.setSubscribeState(subscribeState == 1);
		dto.setSubscribeCount(subscribeCount);
		
		userEntity.getImages().forEach((image)->{
			image.setLikeCount(image.getLikes().size());
		});
		
		return dto;
	}
	
	
	@Transactional
	public User 회원수정(int id, User user) {
		User userEntity = userRepository.findById(id).orElseThrow(()->{ return new CustomValidationApiException("찾을 수 없는 id입니다.");});
   
		userEntity.setName(user.getName());
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		userEntity.setPassword(encPassword);
		userEntity.setBio(user.getBio());
		userEntity.setWebsite(user.getWebsite());
		userEntity.setPhone(user.getPhone());
		userEntity.setGender(user.getGender());
		
		return userEntity;
	}
}










