package com.ecommerce.admin.product.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.admin.AmazonS3Util;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.entity.product.ProductImage;

public class ProductSaveHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

	static void deleteRemovedExtraImageFromFile(Product product) {
		String extraImageDir = "product-images/" + product.getId() + "/extras";
		List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDir);

		for (String objectKey : listObjectKeys) {
			int lastIndexOfSlash = objectKey.lastIndexOf("/");
			String filename = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());

			if (!product.containsImageNames(filename)) {
				AmazonS3Util.deleteFile(objectKey);
			}
		}
	}

	static void setExistingExtraImageNames(String[] imageIds, String[] imageNames, Product product) {
		if (imageIds == null || imageIds.length == 0)
			return;
		Set<ProductImage> images = new HashSet<>();
		for (int count = 0; count < imageIds.length; count++) {
			Integer id = Integer.parseInt(imageIds[count]);
			String name = imageNames[count];
			images.add(new ProductImage(id, name, product));
		}
		product.setImages(images);
	}

	static void setProductDetails(String[] detailIds, String[] detailNames, String[] detailValues, Product product) {
		if (detailNames == null || detailNames.length == 0)
			return;

		for (int count = 0; count < detailNames.length; count++) {
			Integer id = Integer.parseInt(detailIds[count]);
			if (id != 0) {
				product.addDetail(id, detailNames[count], detailValues[count]);
			} else if (!detailNames[count].isEmpty() && !detailValues[count].isEmpty())
				product.addDetail(detailNames[count], detailValues[count]);
		}
	}

	static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Product savedProduct) throws IOException {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "product-images/" + savedProduct.getId();
			
			List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir + "/");
			for (String objectKey : listObjectKeys) {
				if (!objectKey.contains("/extras/")) {
					AmazonS3Util.deleteFile(objectKey);
				}
			}
			
			AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultipart.getInputStream());
		}

		if (extraImageMultiparts.length > 0) {
			String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
				}
			}
		}
	}

	static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if (!product.containsImageNames(fileName))
						product.addExtraImage(fileName);
				}
			}
		}

	}

	static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
}
