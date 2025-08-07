package com.chris.service;

import com.chris.vo.ImageVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    Result<ImageVO> uploadImage(MultipartFile file) throws IOException;

    ResponseEntity<InputStreamResource> getImage(String imageKey);

    String persistTemporaryImage(String tempKey, String merchants);

    void deleteImage(String imageKey);
}
