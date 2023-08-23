package FileUploadApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import FileUploadApplication.exception.ImageFileException;
import FileUploadApplication.model.ImageFile;
import FileUploadApplication.model.User;
import FileUploadApplication.repositories.UserRepository;
import FileUploadApplication.service.ImageFileService;
import jakarta.servlet.http.HttpSession;

@Controller
@ControllerAdvice
public class ImageFileController {
    
	@Autowired
	ImageFileService imageFileService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/save-user")
	public String userPage() {
		return "userForm";
	}
	
	@PostMapping("/save-user") 
	public String saveUser(User user, Model model) {
		  userRepository.save(user);
		  model.addAttribute("message", "User save Successfully");
		  return "userForm";
	}
	
	@GetMapping("/fileupload")
	public String getFileUpload() {
		return "fileupload";
	}
	
	
	@PostMapping("/fileupload") 
	public String uploadImage(
			@Param("username") String username,
			RedirectAttributes re,
			Model model,
			HttpSession session,
			@RequestParam("file") MultipartFile multipartFile) throws ImageFileException {
		
		String message = (String) re.getFlashAttributes().get("message");
		model.addAttribute("message", message);
		
		session.setAttribute("username", username);
		
		User user = userRepository.findByUsername(username);
		if (user == null) {
			model.addAttribute("message", "User Not Exist");
			return "fileupload";
		}
		
		String filename = multipartFile.getOriginalFilename();
		ImageFile imageFile = new ImageFile();
		imageFile.setFilename(filename);
		imageFile.setUser(user);
		imageFileService.storeFile(multipartFile, user, imageFile);
		
		model.addAttribute("message", "save Successfuly");
		return "fileupload";
	}
	
	@GetMapping("/gallery") 
	public String getGallery(Model model, HttpSession session) throws ImageFileException {
		String username = (String)session.getAttribute("username");
		model.addAttribute("username", username);
		User user = userRepository.findByUsername(username);
		List<String> list = imageFileService.loadPaths(user);
		model.addAttribute("path", list);
		
		return "/gallery";
	}
	
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleUploadError(RedirectAttributes re) {
		re.addFlashAttribute("message", "File Exceed the size limit of 200KB");
		return "redirect:/fileupload";
	}
	
	
	
}
