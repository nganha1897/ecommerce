package com.ecommerce.common;

public class Constants {
    public static final String S3_BASE_URI;
    
    static {
    	String bucketName = "ecommerce-files-photos";
    	String pattern = "https://%s.s3.amazonaws.com";
    	
    	String uri = String.format(pattern, bucketName);
    	
    	S3_BASE_URI = bucketName == null ? "" : uri;
    }
}
