package be.uclouvain.lt.pres.ers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "be.uclouvain.lt.pres.ers", "org.openapitools.configuration" })
public class LongTermPreservationWithEvidenceRecordsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(LongTermPreservationWithEvidenceRecordsApplication.class, args);
    }

}
