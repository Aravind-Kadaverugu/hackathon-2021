package com.ncr.hackathon2021.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ncr.hackathon2021.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	User findBy_id(ObjectId _id);
}