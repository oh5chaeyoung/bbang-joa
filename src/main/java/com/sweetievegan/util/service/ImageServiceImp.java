package com.sweetievegan.util.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImp implements ImageService {
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private final AmazonS3 amazonS3;

	public String addOneFile(MultipartFile file, String dirName) {
		String fileName = createFileName(file.getOriginalFilename(), dirName);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());

		try (InputStream inputStream = file.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
		}

		return amazonS3.getUrl(bucket, fileName).toString();
	}

	public List<String> addFile(List<MultipartFile> multipartFile, String dirName) {
		List<String> fileNameList = new ArrayList<>();

		for(MultipartFile file : multipartFile) {
			String result = addOneFile(file, dirName);
			fileNameList.add(result);
		}

		return fileNameList;
	}

	public void removeFile(String fileUrl) {
		String splitStr = ".com/";
		String fileName = fileUrl.substring(fileUrl.lastIndexOf(splitStr) + splitStr.length());
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

	public String createFileName(String fileName, String dirName) {
		return dirName + "/" + UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	public String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
		try {
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
		}
	}
}
