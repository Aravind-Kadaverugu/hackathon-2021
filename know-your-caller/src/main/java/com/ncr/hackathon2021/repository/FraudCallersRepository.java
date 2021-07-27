package com.ncr.hackathon2021.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ncr.hackathon2021.model.FraudCallers;

public interface FraudCallersRepository extends MongoRepository<FraudCallers, String> {
	FraudCallers findBy_id(ObjectId _id);
	
}
