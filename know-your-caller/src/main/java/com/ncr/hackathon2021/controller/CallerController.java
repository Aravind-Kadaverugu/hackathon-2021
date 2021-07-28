package com.ncr.hackathon2021.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Example;
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
import com.ncr.hackathon2021.model.FraudCallers;
import com.ncr.hackathon2021.model.LoadFile;
import com.ncr.hackathon2021.model.User;
import com.ncr.hackathon2021.repository.FraudCallersRepository;
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
    
    @Autowired
    private FraudCallersRepository fraudCallersRepository;
    
    private static final Logger LOG = LoggerFactory.getLogger(CallerController.class);

    @PostMapping("/create-user")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file,
    		@RequestParam("firstName")String firstName,
    		@RequestParam("lastName")String lastName,
    		@RequestParam("emailAddress")String emailAddress,
    		@RequestParam("mobileNumber")String mobileNumber) throws IOException {
    	
    	HttpStatus httpStatus=HttpStatus.BAD_REQUEST;
    	
    	User user= new User(firstName,lastName, emailAddress, mobileNumber);
    	LOG.info("Creating user :"+ user.toString());
    	String fileId=fileService.addFile(file);
    	user.setVoiceNoteId(fileId);
    	User savedUser=null;
    	savedUser = userRepository.save(user);
    	LOG.debug("Created user :"+ savedUser.toString());
    	
    	if(savedUser!=null) {
    		httpStatus=HttpStatus.OK;
    	}
    	return new ResponseEntity<>(savedUser, httpStatus);
    }
    
    
    private FraudCallers getFraudCaller(String callerNumber) {
    	LOG.debug("Checking fraud database for caller : "+callerNumber+" existence.");
    	List<String> fraudMobileNumbers = new ArrayList<String>();
    	fraudMobileNumbers.add(callerNumber);
    	FraudCallers caller =new FraudCallers();
    	caller.setFraudMobileNumbers(fraudMobileNumbers);
    	Optional<FraudCallers> response = fraudCallersRepository.findOne(Example.of(caller));
    	if(response!=null &&
    			!response.isEmpty()) {
    		LOG.debug("Caller with number : "+callerNumber+" found in fraud database !!");
    		return response.get();
    	}
    	return null;
    	
    }
    
    @PostMapping("/identify-fraud")
    public ResponseEntity<?> identifyFraud(@RequestParam("call-recording")MultipartFile file,
    			@RequestParam("incoming-mobile-number")String mobileNumber) throws IOException {
    	LOG.debug("Identifying fraud from caller : "+mobileNumber);
    	FraudAnalysisResponse response=new FraudAnalysisResponse();  
    	if(getFraudCaller(mobileNumber)!=null) {// First check with mobile number : Check for the existence of given mobile number in fraud database.
    		LOG.info("Caller : "+mobileNumber+" exists in fraud database. Not analyzing the call recording anymore.");
    		response.setFraud(true);
    		response.setMessage("Potential Fraud caller!! Please execute caution.");
    	}else { //Second check with call transcription : Convert call recording into transcription and check for fraud keywords.
    		String transcribedCallRecording = speechService.TranscribeCallRecording(file).toLowerCase();
        	LOG.debug("Voice call transcribed : "+transcribedCallRecording);
        	if(transcribedCallRecording.contains("pin")
        			||transcribedCallRecording.contains("otp")
        			||transcribedCallRecording.contains("cvv")
        			||transcribedCallRecording.contains("password")
        			||transcribedCallRecording.contains("word")
        			||transcribedCallRecording.contains("code")) {
        		response.setFraud(true);
        		response.setMessage("Potential Fraud caller!! Please review or report as spam");    		
        		LOG.info("Storing call recording from "+mobileNumber
        				+" for future reference as fraud is detected till end user confirms otherwise.");
        		String fileId=fileService.addFile(file);
        		response.setCallRecordId(fileId);
        	}
        	response.setCallTranscript(transcribedCallRecording);
    	}    	
    	return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/report-fraud-caller")
    public ResponseEntity<?> reportFraudCaller(@RequestParam("record-id")String recordId,
    		@RequestParam("isFraudCaller")boolean isFraudCaller,
    		@RequestParam("fraud-mobile-number")String fraudMobileNumber){
    	try {
    		if(!isFraudCaller) {
	    		LOG.info("Deleting call recording with id : "+recordId +" as the end user reported no fraud.");
				fileService.deleteFile(recordId);
	    	}else {//Adding fraud caller contact number and sample call recording into fraud collection for future references.
	    		List<String> mobileNumbers=new ArrayList<String>();
	    		if(!fraudMobileNumber.contains("+91")) {
	    			fraudMobileNumber="+91"+fraudMobileNumber;
	    		}
	    		mobileNumbers.add(fraudMobileNumber);
	    		FraudCallers fraudCaller=new FraudCallers(mobileNumbers,recordId);
	    		fraudCallersRepository.save(fraudCaller);
	    	}
    	} catch (IOException e) {			
			e.printStackTrace();
		}
    	return new ResponseEntity<>(HttpStatus.OK);
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
