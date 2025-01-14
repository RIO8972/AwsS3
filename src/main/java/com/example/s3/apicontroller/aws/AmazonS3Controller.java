package com.example.s3.apicontroller.aws;

import com.example.s3.service.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Slf4j
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;

    /*
    @PostMapping
    public ResponseEntity<List<String>> uploadFile(List<MultipartFile> multipartFiles){
        return ResponseEntity.ok(awsS3Service.uploadFile(multipartFiles));
    }*/

    /*
    @PostMapping
    public ResponseEntity<String> uploadFile(MultipartFile multipartFile){
        return ResponseEntity.ok((awsS3Service.uploadFile(multipartFile)));
    }*/


    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadFile(MultipartFile multipartFile) {
        // 업로드 처리 후 반환할 데이터 구성
        String fileUrl = awsS3Service.uploadFile(multipartFile);

        // 응답 바디에 파일 URL과 상태 메시지를 포함
        Map<String, Object> response = new HashMap<>();
        response.put("fileUrl", fileUrl);
        response.put("message", "File uploaded successfully");

        return ResponseEntity.ok(response); // 응답 바디와 함께 상태 코드 200 반환
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam String fileName){
        // +) 삭제 시에(fileName) .png 이렇게 이름 + 확장자까지 url 파리미터에 넣어줘야함
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }
}
/*
    메모
    MultipartFile : Spring Framework에서 제공하는 인터페이스로, HTTP요청을 통해 전송된 파일을 다루기
                    위해 사용됨. 주로 파일 업로드 기능을 구현 할 때 사용되며, 클라이언트에서 서버로 파일을
                    전송할 때 그 파일을 처리하는 객체

    주요특징
        1 파일 데이터 처리
             multipart/form-data 인코딩 방식으로 요청을 보낼 때 유용
        2 파일의 메타데이터 접근
            메서드로 추출
        3 파일 내용 읽기
            getInputStream()을 통해 내용을 스트림 형태로 읽음 => 파일 저장 처리에 사용
 */