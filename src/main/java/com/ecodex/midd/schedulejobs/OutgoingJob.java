package com.ecodex.midd.schedulejobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class OutgoingJob implements Job {

    private static final Logger logger = LogManager.getLogger(OutgoingJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("<<<OutgoingJob>>> Job ejecutado - cada 10 segundos");
        // Lógica de tu trabajo que se ejecuta cada 5 segundos
    }
}

