package com.rllc.batch.webcast;

import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableBatchProcessing
@PropertySource({"classpath:batch.properties", "classpath:sftp.properties"})
public class WebcastConfiguration {

	@Autowired
	ItemReader reader;

	@Autowired
	ItemProcessor processor;

	@Autowired
	ItemWriter writer;
	
	@Autowired
	Tasklet timestampTasklet;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	public Job importWebcastScheduleJob() throws MalformedURLException {
		return jobs.get("importWebcastScheduleJob").start(step1()).next(step2()).build();
	}

	@Bean
	public Step step1() throws MalformedURLException {
		return steps.get("step1").chunk(10).reader(reader)
				.processor(processor).writer(writer).build();
	}

	@Bean
	protected Step step2() {
		return steps.get("step2").tasklet(timestampTasklet).build();
	}

}
