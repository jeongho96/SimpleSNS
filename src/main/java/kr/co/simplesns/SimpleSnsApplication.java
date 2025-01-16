package kr.co.simplesns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


// 임시로 DB 자동 연결 막아두기
@SpringBootApplication
public class SimpleSnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSnsApplication.class, args);
    }

}
