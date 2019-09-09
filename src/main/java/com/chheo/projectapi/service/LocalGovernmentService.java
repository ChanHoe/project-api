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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.chheo.projectapi.domain.Organ;
import com.chheo.projectapi.domain.SupportInfo;
import com.chheo.projectapi.dto.SupportInfoDto;
import com.chheo.projectapi.model.CSVData;
import com.chheo.projectapi.repository.OrganRepository;
import com.chheo.projectapi.repository.SupportInfoRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Service
public class LocalGovernmentService {
	private static final Logger LOGGER = LogManager.getLogger(LocalGovernmentService.class);
	
	@Autowired
	private OrganRepository organRepository;
	
	@Autowired
	private SupportInfoRepository supportInfoRepository;
	
	// csv 데이터 read 하여 entity 생성 후 레코드 적재
	public Map<String, String>  setLocalGovernmentData() {
		Map<String, String> map = new HashMap<>();
		map.put("result", "fail");
		
		// csv 데이터를 read 하여 Object 형 list에 담는다
		List<CSVData> csvDataList = this.csvToObject(CSVData.class);
		if(csvDataList != null && csvDataList.size() > 0) {
			int codeCount = 1;
			for (CSVData csvData : csvDataList) {
				LOGGER.info("{}", csvData.toString());
				
				String regionCode = "region" + codeCount;  // 기관 코드
				Organ organ = new Organ(csvData.getRegion(), regionCode); // 기관 엔티티 생성
				
				// 지원한도 : 문자열로 되어있는 지원한도를 숫자로 전환(추천금액은 max 값으로 셋팅...)
				long 	limitValue = this.calcLimitValue(csvData.getLimit());
				LOGGER.info("limit value - {}", limitValue);
				
				// 이차보전 : 문자열 이차보전의 평균 보전율을 구한다(대출이자 전액은 100%로 셋팅...)
				double averageRate = this.calcAverageRate(csvData.getRate());
				LOGGER.info("averageRate value - {}", averageRate);
				
				// 지원정보 엔티티 생성 
				SupportInfo supportInfo =  new SupportInfo(csvData.getTarget(), csvData.getUsage(), csvData.getLimit(), limitValue,
						csvData.getRate(), averageRate,  csvData.getInstitute(), csvData.getMgmt(), csvData.getReception(), organ);
				
				// 엔티티 레코드 저장
				organRepository.save(organ);
				supportInfoRepository.save(supportInfo);
				
				codeCount++;
			}
			map.put("result", "success");
		}
		
		return map;
	}
	
	// 전체 지원정보 data 조회
	public List<SupportInfoDto> getSearchAllData() {
		List<SupportInfoDto> returnList = new ArrayList<>();
		
		List<SupportInfo> infoList = supportInfoRepository.findAll();
		if(infoList != null && infoList.size() > 0) {
			for (SupportInfo info : infoList) {
				SupportInfoDto dto = new SupportInfoDto();
				dto.setRegion(info.getOrgan().getRegion());
				dto.setTarget(info.getTarget());
				dto.setUsage(info.getUsage());
				dto.setLimit(info.getLimited());
				dto.setRate(info.getRate());
				dto.setInstitute(info.getInstitute());
				dto.setMgmt(info.getMgmt());
				dto.setReception(info.getReception());
				
				returnList.add(dto);
			}
		}
		
		return returnList;
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
	
	// 문자 화폐단위를 숫자로 계산하여 지원한도 금액을 숫자로 return(문자열 금액일 경우 xx원 이내 라는 패턴이라고 가정)
	private long calcLimitValue(String limit) {
		// 화폐단위 value
		Map<String, Long> unitMap = new HashMap<>();
		unitMap.put("십", 10L);
		unitMap.put("백", 100L);
		unitMap.put("천", 1000L);
		unitMap.put("만", 10000L);
		unitMap.put("억", 100000000L);
		
		long limitValue = Long.MAX_VALUE;  // limit 이 '추천금액 이내' 문자열일 경우를 대비하여 max 값 설정 
		
		if(limit.matches(".*[0-9].*")) {  // limit 에 숫자가 포함되었을 경우 
			long multiNum = 1L;
			long frontNum = Integer.parseInt(limit.replaceAll("[^0-9]", ""));  // limit 의 숫자
			String unitString = limit.replaceAll("[0-9]", "");  // limit 의 숫자 제외 문자열
			unitString = unitString.substring(0, unitString.indexOf("원"));
			if(unitString.length() > 0) {
				for(int i=0; i<unitString.length(); i++) {
					String searchKey = unitString.charAt(i) + "";
					if(unitMap.containsKey(searchKey)) {
						multiNum *= unitMap.get(searchKey);
					}
				}
			}
			
			limitValue = frontNum * multiNum;
		}
		
		return limitValue;
	}
	
	// 이차보전 평균 비율 계산(숫자일 경우 A% or A% ~ B% 패턴이라고 가정)
	private double calcAverageRate(String rate) {
		double averageRate = 100;  // 대출이자 전액이라는 문자열이 있어 100% 기준으로 잡음 
		if(rate.matches(".*[0-9].*")) {  // rate 에 숫자가 포함되었을 경우
			if(rate.contains("~")) {
				String[] tempArr = rate.replaceAll("%", "").split("~");
				averageRate = (Double.parseDouble(tempArr[0]) + Double.parseDouble(tempArr[1])) / 2;
			} else {
				averageRate = Double.parseDouble(rate.replaceAll("%", ""));
			}
		} 
		
		return averageRate;
	}
}
