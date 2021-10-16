package practice.pdfservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PdfServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfServiceApplication.class, args);
    }
}
