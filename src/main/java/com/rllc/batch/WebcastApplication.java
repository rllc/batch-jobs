package com.rllc.batch;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class WebcastApplication {

	private static final Logger log = LoggerFactory
			.getLogger(WebcastApplication.class);

	public static void main(String[] args) {
		DateTime start = new DateTime();
		SpringApplication.exit(SpringApplication.run(WebcastApplication.class,
				args));

		DateTime end = new DateTime();
		Duration duration = new Interval(start, end).toDuration();
		PeriodFormatter formatter = new PeriodFormatterBuilder()
		     .appendDays()
		     .appendSuffix("d")
		     .appendHours()
		     .appendSuffix("h")
		     .appendMinutes()
		     .appendSuffix("m")
		     .appendSeconds()
		     .appendSuffix("s")
		     .toFormatter();
		String formatted = formatter.print(duration.toPeriod());
		StringBuilder sb = new StringBuilder("\n");
		sb.append("-----------------------------------").append("\n");
		sb.append("> execution time : " + formatted).append("\n");
		sb.append("-----------------------------------").append("\n");
		
		log.info(sb.toString());
	}

}
