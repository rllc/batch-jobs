package com.rllc.batch.webcast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class TimestampTasklet implements Tasklet {
	
	private static final Logger log = LoggerFactory
			.getLogger(TimestampTasklet.class);

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		log.info("saving last run time to properties file..");
		
		PropertiesConfiguration config = new PropertiesConfiguration("batch.properties");
		String lastExecutionTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(Calendar.getInstance().getTime());
		config.setProperty("lastExecutionTime", lastExecutionTime);
		config.save();
		
		log.info("lastExecutionTime : " + lastExecutionTime);
		
		return RepeatStatus.FINISHED;
	}

}
