package org.sang.booksocialnetwork.file;

import jakarta.annotation.Nonnull;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sang.booksocialnetwork.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

	@Value("${application.file.upload.photos-output-path}")
	private String fileUploadPath;

	public String saveFile(
			@Nonnull MultipartFile sourceFile,
			@Nonnull Integer userId) {
		final String fileUploadSubPath="users"+ File.separator +userId;

		return uploadFile(sourceFile,fileUploadSubPath);
	}

	private String uploadFile(
			@Nonnull MultipartFile sourceFile,
			@Nonnull String fileUploadSubPath) {
		final String finalUploadPath=fileUploadPath +File.separator +fileUploadSubPath;
		File targetFolder=new File(finalUploadPath);
		if(!targetFolder.exists()){
			boolean folderCreated=targetFolder.mkdirs();//mkdir tao thu muc don,con mkdirs tạo thu muc va cac thu muc
			// con
			if(!folderCreated){
				log.warn("Failed to create target folder");
				return null;

			}

		}
		final String fileExtension=getFileExtension(sourceFile.getOriginalFilename());
		//./upload/users/1/11120002.jpg
		String targetFilePath=finalUploadPath + File.separator +System.currentTimeMillis() + "." +fileExtension;
		Path targetPath= Paths.get(targetFilePath);
		try{
			Files.write(targetPath,sourceFile.getBytes());
			log.info("File saved to{}", targetFilePath);
		}catch(Exception e){
			log.error("File was not saved",e);
		};

		return null;
	}

	//get type of file (.pdf,.jpg ,...)
	private String getFileExtension(String fileName) {
		if(fileName == null || fileName.isEmpty()){
			return "";
		};
		//lây index tại dấu chấm : sang.jpg -> index 4
		int lastDotIndex=fileName.lastIndexOf(".");
		if(lastDotIndex == -1){
			return "";
		}
		//.JPG ->jpg
		return fileName.substring(lastDotIndex+1).toLowerCase();
	}
}
