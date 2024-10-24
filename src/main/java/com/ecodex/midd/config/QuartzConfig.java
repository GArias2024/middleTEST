package com.ecodex.midd.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecodex.midd.schedulejobs.IncomingJob;
import com.ecodex.midd.schedulejobs.OutgoingJob;

@Configuration
public class QuartzConfig {
	
    @Value("${incomingJob.interval}")
    private int incomingJobInterval;
    
    @Value("${outgoingJob.interval}")
    private int outgoingJobInterval;

    // IncomingJob config
    @Bean
    public JobDetail incomingJobDetail() {
        return JobBuilder.newJob(IncomingJob.class)
                .withIdentity("incomingJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger incomingJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(incomingJobInterval)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(incomingJobDetail())
                .withIdentity("incomingJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    // OutgoingJob config
    @Bean
    public JobDetail outgoingJobDetail() {
        return JobBuilder.newJob(OutgoingJob.class)
                .withIdentity("outgoingJobDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger outgoingJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(outgoingJobInterval)
                .repeatForever(); 

        return TriggerBuilder.newTrigger()
                .forJob(outgoingJobDetail())
                .withIdentity("outgoingJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
    
    
}
