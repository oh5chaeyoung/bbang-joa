package com.sweetievegan.blog.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogImageService {
	List<String> addFile(List<MultipartFile> multipartFile, String dirName);
	void removeFile(String fileName);
	String createFileName(String fileName, String dirName);
	String getFileExtension(String fileName);
}
