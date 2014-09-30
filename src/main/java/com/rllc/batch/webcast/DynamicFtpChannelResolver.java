/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rllc.batch.webcast;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

/**
 * Demonstrates how a dynamic Spring Integration flow snippet can be used
 * to send files to dynamic destinations.
 *
 * @author Gary Russell
 * @author Amol Nayak
 * @since 2.1
 *
 */

@Component
public class DynamicFtpChannelResolver {
	
	private static final Logger log = LoggerFactory
			.getLogger(DynamicFtpChannelResolver.class);
	
	@Autowired
	Environment env;
	
	// In production environment this value will be significantly higher
	// This is just to demonstrate the concept of limiting the max number of
	// Dynamically created application contexts we'll hold in memory when we
	// execute
	// the code from a junit
	public static final int MAX_CACHE_SIZE = 5;

	private final LinkedHashMap<String, MessageChannel> channels = new LinkedHashMap<String, MessageChannel>() {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<String, MessageChannel> eldest) {
			// This returning true means the least recently used
			// channel and its application context will be closed and removed
			boolean remove = size() > MAX_CACHE_SIZE;
			if (remove) {
				MessageChannel channel = eldest.getValue();
				ConfigurableApplicationContext ctx = contexts.get(channel);
				if (ctx != null) { // shouldn't be null ideally
					ctx.close();
					contexts.remove(channel);
				}
			}
			return remove;
		}

	};

	private final Map<MessageChannel, ConfigurableApplicationContext> contexts = new HashMap<MessageChannel, ConfigurableApplicationContext>();

	/**
	 * Resolve a file to a channel, where each file gets a private
	 * application context and the channel is the inbound channel to that
	 * application context.
	 *
	 * @param file
	 * @return a channel
	 */
	public MessageChannel resolve(String fileName) {
		MessageChannel channel = this.channels.get(fileName);
		if (channel == null) {
			channel = createNewFileChannel(fileName);
		}
		return channel;
	}

	private synchronized MessageChannel createNewFileChannel(String fileName) {
		MessageChannel channel = this.channels.get(fileName);
		if (channel == null) {
			ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
					new String[] { "dynamic-ftp-outbound-adapter-context.xml" },
					false);
			this.setEnvironmentForFile(ctx, fileName);
			ctx.refresh();
			channel = ctx.getBean("toFtpChannel", MessageChannel.class);
			this.channels.put(fileName, channel);
			// Will works as the same reference is presented always
			this.contexts.put(channel, ctx);
		}
		return channel;
	}

	/**
	 * Use Spring 3.1. environment support to set properties for the
	 * file-specific application context.
	 *
	 * @param ctx
	 * @param customer
	 */
	private void setEnvironmentForFile(ConfigurableApplicationContext ctx,
			String fileName) {
		StandardEnvironment environment = new StandardEnvironment();
		Properties props = new Properties();

		// populate properties for file
		props.setProperty("host", env.getProperty("batch.sftp.hostname"));
		props.setProperty("user", env.getProperty("batch.sftp.username"));
		props.setProperty("password", env.getProperty("batch.sftp.password"));
		props.setProperty("port", env.getProperty("batch.sftp.port"));
		props.setProperty("remote.directory", getRemoteDirectoryForFile(fileName));
		PropertiesPropertySource pps = new PropertiesPropertySource("ftpprops",
				props);
		environment.getPropertySources().addLast(pps);
		ctx.setEnvironment(environment);
	}

	/**
	 * This method maps a file to a remote directory.
	 * 
	 * @param fileName
	 * @return name of remote directory to upload file to
	 */
	private String getRemoteDirectoryForFile(String fileName) {
		String remoteDirectory = "";
		String rootDirectory = env.getProperty("batch.sftp.dest.rootDirectory");
		String fileDirectory = "Rockford_" + fileName.substring(0, 4);
		remoteDirectory = rootDirectory + fileDirectory;
		
		log.info(fileName + " -> " + remoteDirectory);
		return remoteDirectory;
	}

}
