package com.spring.getready.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spring.getready.model.UploadFile;
import com.spring.getready.repository.UploadFileRepository;

@Service
public class UploadFileService {

	@Autowired
	private UploadFileRepository uploadFileRepository;

	@Value("${file.upload-path}")
	private String uploadPath;

	public UploadFile uploadFile(String fileName, String fileOriginalName) {
		UploadFile uploadFile = new UploadFile();
		uploadFile.setFileName(fileName);
		uploadFile.setFileOriginalName(fileOriginalName);
		uploadFile.setIsDeleted(false);
		uploadFile.setUploadedOn(new Timestamp(new Date().getTime()));
		return uploadFileRepository.save(uploadFile);
	}

	public UploadFile saveFile(MultipartFile file, String username) throws IOException {
		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String savedFilename = UUID.randomUUID().toString() + extension;
		
		Path uploadDir = Paths.get(uploadPath);
		if (!Files.exists(uploadDir)) {
			Files.createDirectories(uploadDir);
		}
		
		Path filePath = uploadDir.resolve(savedFilename);
		Files.copy(file.getInputStream(), filePath);
		
		return uploadFile(savedFilename, originalFilename);
	}

	public String extractTextFromFile(UploadFile uploadFile) {
		try {
			Path filePath = Paths.get(uploadPath, uploadFile.getFileName());
			String content = new String(Files.readAllBytes(filePath));
			return content;
		} catch (IOException e) {
			return "";
		}
	}

}
