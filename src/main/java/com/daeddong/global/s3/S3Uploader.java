package com.daeddong.global.s3;

import com.daeddong.global.exception.DaeddongException;
import com.daeddong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/heic"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.region}")
    private String region;

    /**
     * S3에 이미지를 업로드하고 URL을 반환합니다.
     *
     * @param file   업로드할 이미지 파일
     * @param folder S3 내 저장 폴더 (예: "reviews", "reports")
     * @return 업로드된 이미지의 S3 URL
     */
    public String upload(MultipartFile file, String folder) {
        validate(file);

        String key = folder + "/" + UUID.randomUUID() + extractExtension(file.getOriginalFilename());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new DaeddongException(ErrorCode.S3_UPLOAD_FAILED);
        }

        return buildUrl(key);
    }

    /**
     * S3에서 이미지를 삭제합니다.
     *
     * @param imageUrl 삭제할 이미지의 S3 URL
     */
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String key = extractKey(imageUrl);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    // --- private ---

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DaeddongException(ErrorCode.IMAGE_EMPTY);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new DaeddongException(ErrorCode.IMAGE_TYPE_INVALID);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new DaeddongException(ErrorCode.IMAGE_SIZE_EXCEEDED);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    private String buildUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String extractKey(String imageUrl) {
        // "https://bucket.s3.region.amazonaws.com/folder/filename.jpg" -> "folder/filename.jpg"
        return imageUrl.substring(imageUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
    }
}
