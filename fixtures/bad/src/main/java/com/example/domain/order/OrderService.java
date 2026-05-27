package com.example.domain.order;

// 위반 샘플: 도메인이 프로젝트 infra 어댑터에 의존 + 필드 주입.
import com.example.infra.s3.S3Service;                              // 위반: 도메인 → .infra.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;                     // 실용 모드라 이건 허용

@Service
public class OrderService {

    @Autowired
    private S3Service s3;   // 위반: 필드 주입(@Autowired 필드)

    public void archive(Order order) {
        s3.upload(order);
    }
}
