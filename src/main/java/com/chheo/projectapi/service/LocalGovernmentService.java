package com.chheo.projectapi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.chheo.projectapi.model.CSVData;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Service
public class LocalGovernmentService {
	private static final Logger LOGGER = LogManager.getLogger(LocalGovernmentService.class);
	
	// csv 데이터 read 하여 entity 생성
	public Map<String, String>  setLocalGovernmentData() {
		Map<String, String> map = new HashMap<>();
		map.put("result", "fail");
		
		// csv 데이터를 read 하여 Object 형 list에 담는다
		List<CSVData> csvDataList = this.csvToObject(CSVData.class);
		if(csvDataList != null && csvDataList.size() > 0) {
			for (CSVData csvData : csvDataList) {
				LOGGER.info("{}", csvData.toString());
			}
			map.put("result", "success");
		}
		
		return map;
	}
	
	private <T> List<T> csvToObject(Class<T> responseClass){
	    List<T> resultList = new ArrayList<>();
	    try {
	    	String fileName = "서버개발_사전과제1_지자체협약지원정보_16년12월현재__최종.csv";
			ClassPathResource resource = new ClassPathResource("static/data/" + fileName);
			File file = new File(resource.getURI());
	    	
	    	CsvSchema schema = CsvSchema.builder()
	                .addColumn("division",CsvSchema.ColumnType.STRING)
	                .addColumn("region",CsvSchema.ColumnType.STRING)
	                .addColumn("target",CsvSchema.ColumnType.STRING)
	                .addColumn("usage",CsvSchema.ColumnType.STRING)
	                .addColumn("limit",CsvSchema.ColumnType.STRING)
	                .addColumn("rate",CsvSchema.ColumnType.STRING)
	                .addColumn("institute",CsvSchema.ColumnType.STRING)
	                .addColumn("mgmt",CsvSchema.ColumnType.STRING)
	                .addColumn("reception",CsvSchema.ColumnType.STRING)
	                .build().withHeader();
	    	
	        CsvMapper csvMapper = new CsvMapper();
	        MappingIterator<T> mappingIterator = csvMapper.readerFor(responseClass)
	        		.with(schema).readValues(new InputStreamReader(new FileInputStream(file), "EUC-KR"));

	        resultList =  mappingIterator.readAll();
	        
	    }catch (Exception e){
	    	LOGGER.info("Error  : " + e);
	    }
	    
	    return resultList;
	}
}
