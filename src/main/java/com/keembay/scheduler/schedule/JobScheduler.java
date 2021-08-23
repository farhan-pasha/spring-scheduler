package com.keembay.scheduler.schedule;

import com.bugsnag.Bugsnag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class JobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private Bugsnag bugsnag;

    @Value("${jobs.renewal.enable}")
    private boolean isEnabled;

    @Scheduled(cron="${jobs.renewal.cron}")
    public void scheduler(){
        if (isEnabled) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            try {
                JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                log.info("Job's Status::::" + jobExecution.getStatus());
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                    | JobParametersInvalidException e) {
                bugsnag.notify(e);
                e.printStackTrace();
            }
        }
    }
}
