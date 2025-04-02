package com.ecomerce.sb_ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        //File name of current / original file
        String originalFilename=file.getOriginalFilename();

        // Generate a unique file name
        String randomId= UUID.randomUUID().toString(); //12354
        String fileName=randomId.concat(originalFilename.substring(originalFilename.lastIndexOf("."))); //.jpg
        String filePath=path+ File.separator+fileName; //  File.separator -> \  (we explicitlly mention it because if we directly write / then it wont be understand by Linux
        System.out.println(filePath);//images/\c9e66400-7be3-4abb-893b-d2621c5008db.jpg

        // Check if a path exist and create
        File folder=new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        //upload to a server
        Files.copy(file.getInputStream(), Paths.get(filePath)); // object,destination
        //return a file name
        return fileName;
    }
}
