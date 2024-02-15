package com.ecommerce.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AmazonS3UtilTest {

	@Test
	public void testListFolder() {
		List<String> listKeys = AmazonS3Util.listFolder("product-images/18");
		listKeys.forEach(System.out::println);
	}
	
	@Test
	public void testUploadFile() throws FileNotFoundException {
		String folderName = "test-upload";
		String fileName = "test1.JPG";
		String filePath = "/Users/hatran/Desktop/" + fileName;
		
		InputStream inputStream = new FileInputStream(filePath);
		
		AmazonS3Util.uploadFile(folderName, fileName, inputStream);
	}
	
	@Test
	public void testDeleteFile() {
		String fileName = "test-upload/test.jpeg";
		AmazonS3Util.deleteFile(fileName);
	}
	
	@Test
	public void testDeleteFolder() {
		AmazonS3Util.removeFolder("test-upload");
	}
}
