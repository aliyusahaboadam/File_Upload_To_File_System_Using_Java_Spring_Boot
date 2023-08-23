package FileUploadApplication.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import FileUploadApplication.exception.ImageFileException;
import FileUploadApplication.model.ImageFile;
import FileUploadApplication.model.User;
import FileUploadApplication.repositories.ImageFileRepository;

@Service
public class ImageFileService {
	
	@Autowired
	private ImageFileRepository imageFileRepository;
	
	public void storeFile (MultipartFile file, User user, ImageFile imageFile) throws ImageFileException {
		String fileDir = "src/main/resources/static" + "/" + user.getId();
		Path filePath = Path.of(fileDir);
		
		if (file.isEmpty()) {
			
			throw new ImageFileException("failed to store empty file" + file.getOriginalFilename());
		}
		
		
		    try {
				if (!Files.exists(filePath))  {
				Files.createDirectory(filePath);
				}
				Files.copy(file.getInputStream(), filePath.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
				
			} catch (IOException e) {
				throw new ImageFileException("failed to store file" + file.getOriginalFilename());
			}
		
		imageFileRepository.save(imageFile);
		
	}
	
	public List<String> loadPaths (User user) throws ImageFileException {
		Path filePath = Path.of("src/main/resources/static" + "/" + user.getId());
		
		try {
			return Files.walk(filePath, 2)
			.filter(path -> !path.equals(filePath))
			.map(path -> path.toString().replace(filePath.toString(), "..\\" + user.getId() + "\\"))
			.collect(Collectors.toList());
		} catch (IOException e) {
			throw new ImageFileException("failed to read file stored");
		}
	}

}
