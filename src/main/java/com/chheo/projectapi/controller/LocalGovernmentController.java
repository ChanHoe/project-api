package com.chheo.projectapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chheo.projectapi.common.ApiException;
import com.chheo.projectapi.common.ErrorCode;
import com.chheo.projectapi.model.ApiParameter;
import com.chheo.projectapi.service.LocalGovernmentService;

@RestController
@RequestMapping(value = "/api")
public class LocalGovernmentController {
	
	@Autowired
	private LocalGovernmentService localGovernmentService;
	
	@PostMapping("/info/create")
	public Object createData() {
		return localGovernmentService.setLocalGovernmentData();
	}
	
	@GetMapping("/info/search/all")
	public Object searchAllData() {
		return localGovernmentService.getAllData();
	}
	
	@GetMapping("/info/search/region")
	public Object seachRegionData(@RequestParam(value = "region", required = true) String region) {
		if(region == null || region.equals("")) { 
			throw new ApiException(ErrorCode.INVALID_PARAM);
		}
		
		return localGovernmentService.getRegionData(region);
	}
	
	@PutMapping("/info/modify")
	public Object modifySupportInfo(@RequestBody ApiParameter apiParameter) {
		
		if(apiParameter.getRegion() == null || apiParameter.getRegion().equals("")) {
			throw new ApiException(ErrorCode.INVALID_PARAM);
		}
		
		return localGovernmentService.updateSupportInfo(apiParameter);
	}
	
	@GetMapping("/info/sort/region")
	public Object searchSortAndLimitRegion(@RequestParam(value = "size", required = true) int size) {
		if(size <= 0) {
			throw new ApiException(ErrorCode.INVALID_PARAM);
		}
		
		return localGovernmentService.getSortAndLimitRegion(size);
	}
	
	@GetMapping("/info/atleast/rate")
	public Object searchAtleastRateInstitute() {
		return localGovernmentService.getAtleastRateInsitute();
	}
	
}
