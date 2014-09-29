package com.rllc.batch.webcast;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class WebcastProcessor implements ItemProcessor<File, File> {

	private static final Logger log = LoggerFactory
			.getLogger(WebcastProcessor.class);
	
	@Override
	public File process(File file) throws Exception {

		log.info("+++++++++++++++++++++++++++++++++++++++++");
		log.info("processing : " + file.getName());
		log.info("+++++++++++++++++++++++++++++++++++++++++");
		
		return file;
	}

}