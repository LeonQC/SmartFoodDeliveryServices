package com.chris.service.impl;

import com.chris.config.S3Config;
import com.chris.service.S3Service;
import com.chris.vo.ImageVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    // 下面三个字段就安全地在构造时初始化了
    private final String bucket;
    private final S3Client client;

    // Spring 会自动把 userRepository 和 s3BucketClient 注入进来
    public S3ServiceImpl(S3Config.S3BucketClient s3BucketClient) {
        this.bucket            = s3BucketClient.bucket();
        this.client            = s3BucketClient.client();
    }

    @Override
    @Transactional
    public Result<ImageVO> uploadImage(MultipartFile file) throws IOException {
        // 创建图片上传的 key，到 temp 逻辑目录下，真正提交写库时再换成图片逻辑目录
        String key = "temp/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        client.putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        ImageVO vo = new ImageVO();
        vo.setUrl(key);
        return Result.success(vo);
    }

    @Override
    public ResponseEntity<InputStreamResource> getImage(String imageKey) {
        ResponseInputStream<GetObjectResponse> stream = client
                .getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(imageKey)
                        .build());

        MediaType type = MediaType.parseMediaType(stream.response().contentType());
        return ResponseEntity.ok()
                .contentType(type)
                .body(new InputStreamResource(stream));
    }

    /**
     * 将 temp 前缀下的对象搬迁到业务前缀（如 users/{userId}/…、dishes/{dishId}/…），
     * 并删除临时文件。返回新的永久 URL。
     *
     * @param tempKey      不带域名的 temp 对象 key（比如 "temp/1234_img.png"）
     * @param targetPrefix 业务前缀（比如 "users/42/avatar" 或 "dishes/17/images"）
     */
    @Override
    public String persistTemporaryImage(String tempKey, String targetPrefix) {
        // 1. 计算新 key
        String filename = tempKey.substring(tempKey.lastIndexOf('/') + 1);
        String permKey  = targetPrefix + "/" + filename;

        // 2. 复制到永久前缀（使用非废弃 API）
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucket)           // 指定源 Bucket
                .sourceKey(tempKey)             // 指定源 Key
                .destinationBucket(bucket)      // 目标 Bucket（同一个或不同）
                .destinationKey(permKey)        // 目标 Key
                .build();
        client.copyObject(copyReq);

        // 3. 删除临时对象 （如果是更新图片，则是删除旧permKey）
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(tempKey)
                .build();
        client.deleteObject(delReq);

        // 4. 返回新的访问 URL
        return permKey;
    }

    /**
     * 删除图片
     *
     * @param imageKey 带域名已持久的图片对象 key（比如 "users/42/avatar/img.png"）
     */
    @Override
    public void deleteImage(String imageKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .build();
        client.deleteObject(request);
    }
}
