package com.ecomerce.sb_ecom.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(String path, MultipartFile file) throws IOException;
}
