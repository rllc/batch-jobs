package com.rllc.batch.webcast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class WebcastFileReader implements ItemReader<File> {

	private static final Logger log = LoggerFactory
			.getLogger(WebcastFileReader.class);
	
	private Stack<File> fileStack;
	
	private Date lastExecutionTime;
	
	private String rootDirectory;
	
	private PropertiesConfiguration config;
	
	@PostConstruct
	public void init() throws Exception {
		log.debug("init");
		config = new PropertiesConfiguration("batch.properties");
		if (config.getString("lastExecutionTime") != null) {
			log.info("lastExecutionTime loaded from file.");
			lastExecutionTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(config.getString("lastExecutionTime"));
		}
		else {
			log.info("lastExecutionTime not found. setting year to 2000");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2000);
			lastExecutionTime = cal.getTime();
		}
		rootDirectory = config.getString("rootDirectory");
		log.info("lastExecutionTime : " + lastExecutionTime);
		log.info("rootDirectory : " + rootDirectory);
		
		fileStack = new Stack<File>();
		find_files(new File(rootDirectory));
	}
	
	public boolean passesFilters(File file) {
		boolean passes = true;
		LocalDate lastTime = new LocalDate(lastExecutionTime);
		LocalDate fileTime = new LocalDate(file.lastModified());
		
		if (!file.isFile()) {
			passes = false;
			log.debug(file.getName() + " is not a file");
		}
		else if (!FilenameUtils.getExtension(file.getName()).equals("mp3")) {
			passes = false;
			log.debug(file.getName() + " is not an mp3 file");
		}
		else if (!fileTime.isAfter(lastTime)) {
			passes = false;
			log.info(file.getName() + " [" + fileTime + "] is not after [" + lastTime + "]");
		}
		
		return passes;
	}

	public void find_files(File root)
	{
	    File[] files = root.listFiles(); 
	    for (File file : files) {
	        if (passesFilters(file)) {
	        	fileStack.push(file);
	        } else if (file.isDirectory()) {
	            find_files(file);
	        }
	    }
	}
	
	@Override
	public File read() {
		if (fileStack.isEmpty()) {
			return null;
		}
		return fileStack.pop();
	}

}