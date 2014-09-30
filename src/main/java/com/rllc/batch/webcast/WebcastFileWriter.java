package com.rllc.batch.webcast;

import java.io.File;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class WebcastFileWriter implements ItemWriter<File> {

	private static final Logger log = LoggerFactory
			.getLogger(WebcastFileWriter.class);
	
	@Autowired
	DynamicFtpChannelResolver channelResolver;
	
	@PostConstruct
	public void init() {
	}
	
	@Override
	public void write(List<? extends File> files) throws Exception {
		for (File file : files) {
			log.info("uploading file : " + file);
			MessageChannel ftpChannel = channelResolver.resolve(file.getName());
			
	        Message<File> message = MessageBuilder.withPayload(file).build();
	        ftpChannel.send(message);
			
		}
	}

}