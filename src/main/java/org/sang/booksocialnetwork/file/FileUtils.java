package org.sang.booksocialnetwork.file;

import io.micrometer.common.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	public static byte[] readFileFromLocation(String fileUrl) {
		//check fileUrl is null
		if(StringUtils.isBlank(fileUrl)){
			return null;
		}
		try {
			Path filePath=new File(fileUrl).toPath();
			return Files.readAllBytes(filePath);
		}catch (IOException e){
			log.warn("No file found in the path{}",fileUrl);
		}
		return null;
	}
}
