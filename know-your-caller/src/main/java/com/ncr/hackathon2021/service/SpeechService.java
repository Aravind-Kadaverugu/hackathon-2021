package com.ncr.hackathon2021.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

@Service
public class SpeechService {
	
	private SpeechClient speechClient;
	
	private static final Logger LOG = LoggerFactory.getLogger(SpeechService.class);
	
	public SpeechService() throws FileNotFoundException, IOException {
		File keyFile = new ClassPathResource("key.json").getFile();
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(
				  ServiceAccountCredentials.fromStream(
						  new FileInputStream(keyFile)));

		  SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
	      speechClient = SpeechClient.create(settings);
	}

	public String TranscribeCallRecording(MultipartFile callRecording) {
		
		String transcriptionResponse="";
		
		try {
			
	    ByteString audioBytes = ByteString.copyFrom(callRecording.getBytes());

	    // Builds the sync recognize request
	    RecognitionConfig config =
	        RecognitionConfig.newBuilder()
	            .setEncoding(AudioEncoding.AMR)
	            .setSampleRateHertz(8000)
	            .setEnableAutomaticPunctuation(true)
	            .setEnableWordTimeOffsets(true)
	            .setLanguageCode("en-US")
	            .setModel("phone_call")
	            .build();
	    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
	    
	    // Use non-blocking call for getting file transcription
	    OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
	    		speechClient.longRunningRecognizeAsync(config, audio);
	    while (!response.isDone()) {
	    	LOG.debug("Waiting for response...");	      
			Thread.sleep(10000);		
	    }

	    List<SpeechRecognitionResult> results = response.get().getResultsList();

	    for (SpeechRecognitionResult result : results) {
	      SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
	      LOG.debug("Transcription: %s\n", alternative.getTranscript());
	      transcriptionResponse+=alternative.getTranscript();
	    }
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //speechClient.close();
		return transcriptionResponse;
	}


	    
	    
}
