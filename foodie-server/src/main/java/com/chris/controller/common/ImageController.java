package com.chris.controller.common;

import com.chris.context.UserContext;
import com.chris.service.S3Service;
import com.chris.vo.ImageVO;
import com.chris.vo.resultVOs.Result;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@Tag(name = "ImageController", description = "Image upload and view API")
public class ImageController {
    @Autowired
    private S3Service s3Service;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    @PostMapping("/upload")
    @Operation(summary = "Upload image", description = "Upload image to S3 bucket")
    public Result<ImageVO> uploadImage(@RequestParam MultipartFile file) throws IOException {
        Long userId = UserContext.getCurrentId();
        String limiterName = (userId == null) ? "registerUpload" : "userUpload";
        RateLimiter limiter  = rateLimiterRegistry.rateLimiter(limiterName);

        // 立即尝试获取许可
        if (!limiter.acquirePermission()) {
            String msg = (userId == null)
                    ? "注册上传太频繁，请稍后再试"
                    : "上传太频繁，请稍候再试";
            return Result.error(msg);
        }
        return s3Service.uploadImage(file);
    }

    @GetMapping
    @Operation(summary = "Get image", description = "Get image from S3 bucket")
    public ResponseEntity<InputStreamResource> getImage(@RequestParam("key") String imageKey) throws IOException {
        return s3Service.getImage(imageKey);
    }

}
