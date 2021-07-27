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
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
/*import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechContext;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;*/
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeRequest;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechContext;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.google.protobuf.ByteString;


@Service
public class SpeechService {
	
	private SpeechClient speechClient;
	
	private static final Logger LOG = LoggerFactory.getLogger(SpeechService.class);
	File keyFile;
	CredentialsProvider credentialsProvider;
	public SpeechService() throws FileNotFoundException, IOException {
		keyFile = new ClassPathResource("key.json").getFile();
		credentialsProvider = FixedCredentialsProvider.create(
				  ServiceAccountCredentials.fromStream(
						  new FileInputStream(keyFile)));
	      
	}

	public String TranscribeCallRecording(MultipartFile callRecording) {
		
		String transcriptionResponse="";
		try {
			SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
			speechClient = SpeechClient.create(settings);
			
			SpeechContext speechContext =
			          SpeechContext.newBuilder().addPhrases("$otp")
			          							.addPhrases("$pin")
			          							.addPhrases("$cvv")
			          							.addPhrases("$otp number")
			          							.addPhrases("$pin number")
			          							.addPhrases("$cvv number")
			          							.addPhrases("$password")
			          							.setBoost(20.0F).build();

		    // The language of the supplied audio
		    String languageCode = "en-US";
		    RecognitionConfig config =
		        RecognitionConfig.newBuilder()
		         .setModel("phone_call")
		         .setLanguageCode(languageCode)
		         .addSpeechContexts(speechContext)
		         .setEncoding(AudioEncoding.AMR)
		         .setSampleRateHertz(8000)
		         .build();
		    ByteString content = ByteString.copyFrom(callRecording.getBytes());
		    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(content).build();		    
		 // Use non-blocking call for getting file transcription
		    OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
		    		speechClient.longRunningRecognizeAsync(config, audio);
		    while (!response.isDone()) {
		    	LOG.info("Waiting for response...");	      
				Thread.sleep(5000);		
		    }

		    List<SpeechRecognitionResult> results = response.get().getResultsList();

		    for (SpeechRecognitionResult result : results) {
		      SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
		      LOG.info("Transcription: "+alternative.getTranscript());
		      LOG.info("Confidence Level:"+alternative.getConfidence());
		      transcriptionResponse+=alternative.getTranscript();
		    }
		  } catch (Exception exception) {
			  LOG.error("Failed to create the client due to: " + exception.getMessage());
			  exception.printStackTrace();
		  }
		speechClient.close();
		return transcriptionResponse;
	}


	    
	    
}
