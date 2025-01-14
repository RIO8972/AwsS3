package com.example.s3.service.aws;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /*
    public List<String> uploadFile(List<MultipartFile> multipartFiles){
        List<String> fileNameList = new ArrayList<>();

        // forEach 구문을 통해 multipartFiles 리스트로 넘어온 파일들을 순차적으로 fileNameList 에 추가
        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()){
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
            fileNameList.add(fileName);

        });

        return fileNameList;
    }*/


    public String uploadFile(MultipartFile multipartFile){

        if (multipartFile == null || multipartFile.isEmpty()) {
            log.info("faillUrl____");
            return null;
        }

        //새로운 파일 이름 지정(createFileName에 추가 설명)
        String fileName = createFileName(multipartFile.getOriginalFilename());
        //파일 메타데이터 생성
        /*
            메타데이터란? 데이터의 대한 설명 정보
            ex) 파일A의 메타데이터
                - 파일크기 / - 파일형식 / -파일 생성,수정일 / -파일이름 등등
         */
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //업로드할 파일의 크기를 바이트 단위로 설정(업로드 할 파일의 크기로)
        objectMetadata.setContentLength(multipartFile.getSize());
        //파일의 콘텐츠 타입을 설정. 예를 들어, 이미지 파일이면 "image/png", "image/jpeg" 등의 값이 될 수 있음 (업로드 파일 타입)
        objectMetadata.setContentType(multipartFile.getContentType());

        //multipartFile.getInputStream()스트림 : 업로드할 파일의 입력 스트림을 가져옴
        try(InputStream inputStream = multipartFile.getInputStream()){
            //amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
            //        .withCannedAcl(CannedAccessControlList.PublicRead));

            //PutObjectRequest는 S3버킷에 파일을 업로드하는 요청객체
            /*
                bucket: 업로드할 대상으로 S3버킷에 파일을 업로드하는 요청 객체
                fileName : 파일 원본 이름
                objectMetadata: 파일에 대한 메타데이터
                amazonS3.putObject : 이 메서드는 파일을 S3로 올리는 작업을 수행함
             */
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        //s3에서 새롭게 저장한 이름
        return fileName;
    }

    //UUID 파일명 생성 메서드
    public String createFileName(String fileName){
        // UUID.randomUUID().toString()로 uuid 객체 생성 + getFileExtension으로 확장자 추가
        // => 새로운 파일명.확장자 반환
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
        /*
            1c90aa54-f3d1-431d-977e-fbc1e1328ed6.png (UUID 방식)
            이렇게하는 이유?
                1. 파일이름중복 방지
                2. 보안(이름으로 파일 유추 방지)
                3. 일관된 이름으로 유지/보수 용이
                4. 검색 최적화
            +)
            UUID란? "범용 고유 식별자"로, 네트워크 상에서 고유한 값을 생성하는 방법
            => 고유한 식별자가 필요할 때 사용함 ( 파일이름 / db 항목실별 / 세션 ID)
         */
    }

    //  "."의 존재 유무만 판단 => 파일 확장자 추출 메서드
    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){ //.없으면 예외 던짐
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }


    public void deleteFile(String fileName){
        // bucket : 삭제할 s3버캣 이름 / fileName : s3버캣에 저장된 파일
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            System.out.println("File " + fileName + " has been deleted from bucket " + bucket);
        } catch (AmazonServiceException e) {
            System.err.println("AmazonServiceException: " + e.getMessage());
        } catch (AmazonClientException e) {
            System.err.println("AmazonClientException: " + e.getMessage());
        }
        //amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        //System.out.println(bucket);

    }
}
