package com.demo.fileUpload.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.demo.fileUpload.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	User findBy_id(ObjectId _id);
}