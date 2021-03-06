package com.blogpic.fileserver.repository;

import com.blogpic.fileserver.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, String> {
}
