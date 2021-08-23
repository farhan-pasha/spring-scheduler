package com.keembay.scheduler.job.config;

import com.keembay.scheduler.dto.ArchiveNotificationDTO;
import com.keembay.scheduler.dto.UserRenewalDTO;
import com.keembay.scheduler.job.processor.RestUserRenewalProcessor;
import com.keembay.scheduler.job.reader.RestUserRenewalReader;
import com.keembay.scheduler.job.writer.RestUserRenewalWriter;
import com.keembay.scheduler.dto.UserRenewalDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class RestUserRenewalJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Value("${jobs.renewal.chunk.size}")
    private int chunkSize;

    @Bean
    public ItemReader<UserRenewalDTO> reader() {
        return new RestUserRenewalReader();
    }

    @Bean
    public ItemProcessor<UserRenewalDTO, ArchiveNotificationDTO> process(){
        return new RestUserRenewalProcessor();
    }
    @Bean
    public ItemWriter<ArchiveNotificationDTO> writer() {
        return new RestUserRenewalWriter();
    }

    @Bean
    public Step userNotification(){
        return stepBuilderFactory.get("userNotification")
                .<UserRenewalDTO, ArchiveNotificationDTO>chunk(chunkSize)
                .reader(reader())
                .processor(process())
                .writer(writer())
                .build();
    }

    @Bean
    public Job runJob(){
        log.info("Calling job");
        return jobBuilderFactory.get("userRenewal")
                .incrementer(new RunIdIncrementer())
                .flow(userNotification())
                .end()
                .build();
    }
}
