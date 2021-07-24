package com.demo.fileUpload.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.fileUpload.model.FraudAnalysisResponse;
import com.demo.fileUpload.model.LoadFile;
import com.demo.fileUpload.model.User;
import com.demo.fileUpload.repository.UserRepository;
import com.demo.fileUpload.service.FileService;

@RestController
@CrossOrigin("*")
@RequestMapping("know-your-caller")
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-user")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file,
    		@RequestParam("firstName")String firstName,
    		@RequestParam("lastName")String lastName,
    		@RequestParam("emailAddress")String emailAddress,
    		@RequestParam("mobileNumber")String mobileNumber) throws IOException {
    	
    	User user= new User(firstName,lastName, emailAddress, mobileNumber);
    	String fileId=fileService.addFile(file);
    	user.setVoiceNoteId(fileId);
    	User savedUser=null;
    	savedUser = userRepository.save(user);
    	
    	if(savedUser!=null) {
    		return new ResponseEntity<>(savedUser, HttpStatus.OK);
    	}
    	return new ResponseEntity<>(savedUser, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/identify-fraud")
    public ResponseEntity<?> identifyFraud(@RequestParam("call-recording")MultipartFile file) throws IOException {
    	
    	FraudAnalysisResponse response=null;
    	
    	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    

    @GetMapping("/get-voice-note/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        LoadFile loadFile = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }
    
    

}
