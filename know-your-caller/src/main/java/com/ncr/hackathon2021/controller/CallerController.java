package com.ncr.hackathon2021.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ncr.hackathon2021.model.FraudAnalysisResponse;
import com.ncr.hackathon2021.model.LoadFile;
import com.ncr.hackathon2021.model.User;
import com.ncr.hackathon2021.repository.UserRepository;
import com.ncr.hackathon2021.service.FileService;
import com.ncr.hackathon2021.service.SpeechService;

@RestController
@CrossOrigin("*")
@RequestMapping("know-your-caller")
public class CallerController {

    @Autowired
    private FileService fileService;
    
    @Autowired
    private SpeechService speechService;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final Logger LOG = LoggerFactory.getLogger(CallerController.class);

    @PostMapping("/create-user")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file,
    		@RequestParam("firstName")String firstName,
    		@RequestParam("lastName")String lastName,
    		@RequestParam("emailAddress")String emailAddress,
    		@RequestParam("mobileNumber")String mobileNumber) throws IOException {
    	
    	User user= new User(firstName,lastName, emailAddress, mobileNumber);
    	LOG.info("Creating user :"+ user.toString());
    	String fileId=fileService.addFile(file);
    	user.setVoiceNoteId(fileId);
    	User savedUser=null;
    	savedUser = userRepository.save(user);
    	LOG.debug("Created user :"+ savedUser.toString());
    	
    	if(savedUser!=null) {
    		return new ResponseEntity<>(savedUser, HttpStatus.OK);
    	}
    	return new ResponseEntity<>(savedUser, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/identify-fraud")
    public ResponseEntity<?> identifyFraud(@RequestParam("call-recording")MultipartFile file,@RequestParam("incoming-mobile-number")String mobileNumber) throws IOException {
    	LOG.debug("Identifying fraud from caller : "+mobileNumber);
    	FraudAnalysisResponse response=new FraudAnalysisResponse();    	
    	String transcribeCallRecording = speechService.TranscribeCallRecording(file);
    	LOG.debug("Voice call transcribed : "+transcribeCallRecording);
    	response.setCallTranscript(transcribeCallRecording);
    	return new ResponseEntity<>(response, HttpStatus.OK);
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
