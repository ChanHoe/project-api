package com.chheo.projectapi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.chheo.projectapi.common.ErrorCode;
import com.chheo.projectapi.domain.Organ;
import com.chheo.projectapi.domain.SupportInfo;
import com.chheo.projectapi.dto.SupportInfoDto;
import com.chheo.projectapi.model.ApiParameter;
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
		File file = null;
		try {
			String fileName = "서버개발_사전과제1_지자체협약지원정보_16년12월현재__최종.csv";
			ClassPathResource resource = new ClassPathResource("static/data/" + fileName);
			file = new File(resource.getURI());
		} catch (Exception e) {
			LOGGER.info("File Error - {}", e);
		}
		
		List<CSVData> csvDataList = new ArrayList<>();
		if(file != null) {
			csvDataList = this.csvToObject(CSVData.class, file);
		}
		if(csvDataList != null && csvDataList.size() > 0) {
			int codeCount = 1;
			for (CSVData csvData : csvDataList) {
				LOGGER.info("{}", csvData.toString());
				String regionCode = "region" + codeCount;  // 기관 코드
				
				// 기관 생성
				Organ organ = new Organ();
				organ.setRegion(csvData.getRegion());
				organ.setRegionCode(regionCode);
				
				// 지원한도 : 문자열로 되어있는 지원한도를 숫자로 전환(추천금액은 max 값으로 셋팅...)
				long 	limitValue = this.calcLimitValue(csvData.getLimit());
				LOGGER.info("limit value - {}", limitValue);
				
				// 이차보전 : 문자열 이차보전의 평균 보전율을 구한다(대출이자 전액은 100%로 셋팅...)
				double averageRate = this.calcAverageRate(csvData.getRate());
				LOGGER.info("averageRate value - {}", averageRate);
				
				// 지원정보 생성
				SupportInfo info = new SupportInfo();
				info.setTarget(csvData.getTarget());
				info.setUsage(csvData.getUsage());
				info.setLimited(csvData.getLimit());
				info.setLimitValue(limitValue);
				info.setRate(csvData.getRate());
				info.setAverageRate(averageRate);
				info.setInstitute(csvData.getInstitute());
				info.setMgmt(csvData.getMgmt());
				info.setReception(csvData.getReception());
				info.setOrgan(organ);
				
				// add info -> organ 
				organ.getSupportInfoList().add(info);
				//supportInfoRepository.save(info);
				
				// entity record 생성
				organRepository.save(organ);
				
				codeCount++;
			}
			map.put("result", "success");
		}
		
		return map;
	}
	
	// 전체 지원정보 data 조회
	public List<SupportInfoDto> getAllData() {
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
	
	// 지자체명으로 지원정보 search
	public Map<String, Object> getRegionData(String region) {
		Map<String, Object> map = new HashMap<>();
		map.put("result", "fail");
		Organ organ = organRepository.findByRegion(region);
		if(organ != null) {
			List<SupportInfo> infoList = organ.getSupportInfoList();
			if(infoList.size() > 0) {
				List<SupportInfoDto> returnList = new ArrayList<>();
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
				
				map.put("result", "success");
				map.put("data", returnList);
			} else {
				map.put("message", ErrorCode.NOT_FOUND.getMessage());
			}
		} else {
			map.put("message", ErrorCode.NOT_FOUND.getMessage());
		}
		
		return map;
	}
	
	// 지원정보 data 수정
	@Transactional
	public Map<String, Object> updateSupportInfo(ApiParameter param) {
		Map<String, Object> map = new HashMap<>();
		
		Organ organ = organRepository.findByRegion(param.getRegion());
		if(organ != null) {
			List<SupportInfo> findInfoList = organ.getSupportInfoList();
			if(findInfoList.size() > 0) {
				List<SupportInfoDto> returnList = new ArrayList<>();
				for (SupportInfo info : findInfoList) {		
					SupportInfo findInfo = supportInfoRepository.findById(info.getId()).get();
					
					long limitValue = this.paramCheck(param.getLimit()) ? this.calcLimitValue(param.getLimit()) : findInfo.getLimitValue();
					double averageRate =  this.paramCheck(param.getRate()) ? this.calcAverageRate(param.getRate()) : findInfo.getAverageRate();
					findInfo.setTarget(this.paramCheck(param.getTarget()) ? param.getTarget() : findInfo.getTarget());
					findInfo.setUsage(this.paramCheck(param.getUsage()) ? param.getUsage() : findInfo.getUsage());
					findInfo.setLimited(this.paramCheck(param.getLimit()) ?  param.getLimit() : findInfo.getLimited());
					findInfo.setLimitValue(limitValue);
					findInfo.setRate(this.paramCheck(param.getRate()) ?  param.getRate() : findInfo.getRate());
					findInfo.setAverageRate(averageRate);
					findInfo.setInstitute(this.paramCheck(param.getInstitute()) ?  param.getInstitute() : findInfo.getInstitute());
					findInfo.setMgmt(this.paramCheck(param.getMgmt()) ? param.getMgmt() : findInfo.getMgmt());
					findInfo.setReception(this.paramCheck(param.getReception()) ? param.getReception() : findInfo.getReception());
					supportInfoRepository.save(findInfo);
					
					SupportInfo updateInfo = supportInfoRepository.findById(info.getId()).get();
					SupportInfoDto dto = new SupportInfoDto();
					dto.setRegion(updateInfo.getOrgan().getRegion());
					dto.setTarget(updateInfo.getTarget());
					dto.setUsage(updateInfo.getUsage());
					dto.setLimit(updateInfo.getLimited());
					dto.setRate(updateInfo.getRate());
					dto.setInstitute(updateInfo.getInstitute());
					dto.setMgmt(updateInfo.getMgmt());
					dto.setReception(updateInfo.getReception());
					returnList.add(dto);
				}
				
				map.put("result", "success");
				map.put("message", "수정에 성공 하였습니다");
				map.put("data", returnList);
				
			} else {
				map.put("message", ErrorCode.NOT_FOUND.getMessage());
			}
		} else {
			map.put("message", ErrorCode.NOT_FOUND.getMessage());
		}
		
		return map;
	}
	
	// 지원금액 내림차순 정렬(동일시 이차보전 평균 비율 오름차순) limit value
	public Map<String, Object> getSortAndLimitRegion(int size) {
		Map<String, Object> map = new HashMap<>();
		map.put("result", "fail");
		
		Pageable pageable = PageRequest.of(0, size, new Sort(Sort.Direction.DESC, "limitValue").and(new Sort(Sort.Direction.ASC, "averageRate")));
		List<SupportInfo> findInfoList = supportInfoRepository.findAll(pageable).getContent();
		if(findInfoList != null && findInfoList.size() > 0) {
			List<String> returnList = new ArrayList<>();
			for (SupportInfo info : findInfoList) {
				returnList.add(info.getOrgan().getRegion());
			}
			
			map.put("result", "success");
			map.put("data", returnList);
			
		} else {
			map.put("message", ErrorCode.NOT_FOUND.getMessage());
		}
		
		return map;
	}
	
	// 이차보전 비율이 가장 작은 추천 기관
	public Map<String, Object> getAtleastRateInsitute() {
		Map<String, Object> map = new HashMap<>();
		map.put("result", "fail");
		
		Pageable pageable = PageRequest.of(0, 1, new Sort(Sort.Direction.ASC, "averageRate"));
		List<SupportInfo> findInfoList = supportInfoRepository.findAll(pageable).getContent();
		if(findInfoList != null && findInfoList.size() > 0) {
			map.put("result", "success");
			map.put("data",findInfoList.get(0).getInstitute());
			
		} else {
			map.put("message", ErrorCode.NOT_FOUND.getMessage());
		}
		
		return map;
	}
	
	// csv data read 하여 Object 변환 후 List에 담아 return
	private <T> List<T> csvToObject(Class<T> responseClass, File file){
	    List<T> resultList = new ArrayList<>();
	    try {
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
	
	// parameter null or empty check
	private boolean paramCheck(String param) {
		return param != null && !param.equals("") ? true : false;
	}
}
