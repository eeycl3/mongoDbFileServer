package com.blogpic.fileserver.model;

import java.util.Date;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@Document
public class File {
	@Id  // primary key
	private String id;
    private String name; // file name
    private String contentType; // file type
    private long size;
    private Date uploadDate;
    private String md5;
    private byte[] content; // content
    private String path; // path

    public File(String name, String contentType, long size, byte[] content) {
    	this.name = name;
    	this.contentType = contentType;
    	this.size = size;
    	this.uploadDate = new Date();
    	this.content = content;
    }
}
