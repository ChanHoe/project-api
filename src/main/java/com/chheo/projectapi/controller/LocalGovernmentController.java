package com.chheo.projectapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chheo.projectapi.service.LocalGovernmentService;

@RestController
@RequestMapping(value = "/local")
public class LocalGovernmentController {
	
	@Autowired
	private LocalGovernmentService localGovernmentService;
	
	@PostMapping("/create")
	public @ResponseBody Object createData() {
		return localGovernmentService.setLocalGovernmentData();
	}
	
	@GetMapping("/all")
	public @ResponseBody Object searchAllData() {
		return localGovernmentService.getSearchAllData();
	}
}
