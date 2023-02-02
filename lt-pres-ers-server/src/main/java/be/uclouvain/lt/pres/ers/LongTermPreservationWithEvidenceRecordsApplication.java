package be.uclouvain.lt.pres.ers;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S") // TODO : find a proper duration ?
@ComponentScan(basePackages = { "be.uclouvain.lt.pres.ers", "org.openapitools.configuration" })
public class LongTermPreservationWithEvidenceRecordsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(LongTermPreservationWithEvidenceRecordsApplication.class, args);
    }

}
