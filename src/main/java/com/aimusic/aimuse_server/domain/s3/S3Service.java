package com.aimusic.aimuse_server.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * S3에 파일을 업로드하고, 저장된 S3 키(Key)를 반환
     * @param multipartFile 업로드할 파일
     * @param dirName S3 버킷 내부에 생성될 디렉토리 이름 (예: "music/raw")
     * @return S3에 저장된 파일의 전체 경로 (S3 Key)
     */
    public String uploadFile(MultipartFile multipartFile, String dirName) throws IOException {

        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFilename = dirName + "/" + UUID.randomUUID() + "_" + originalFilename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFilename)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            return uniqueFilename;

        } catch (S3Exception e) {
            System.err.println("S3 업로드 오류: " + e.awsErrorDetails().errorMessage());
            throw new IOException("S3 업로드에 실패했습니다. AWS 권한/설정을 확인하세요.", e);
        } catch (IOException e) {
            throw new IOException("파일 스트림 처리 중 오류 발생", e);
        }
    }
}