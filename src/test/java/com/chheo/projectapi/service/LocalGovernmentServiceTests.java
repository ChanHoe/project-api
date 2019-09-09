package com.chheo.projectapi.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalGovernmentServiceTests {
	Logger logger = LogManager.getLogger(LocalGovernmentServiceTests.class);
	
	@Autowired
	private LocalGovernmentService localGovernmentService;

	@Test
	public void CSV_DATA_READ_테스트() {
		Map<String, String> map = localGovernmentService.setLocalGovernmentData();
		logger.info("result - {}", map.get("result"));
		assertThat(map.get("result"), is("success"));
	}

}
