package com.chheo.projectapi.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.chheo.projectapi.model.ApiParameter;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocalGovernmentControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testCreateDataSuccess() throws Exception {
		mockMvc.perform(
				post("/api/info/create")
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	@Test
	public void testSearchAllSuccess() throws Exception {
		mockMvc.perform(
				get("/api/info/search/all")
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isOk());
	}
	
	@Test
	public void testSearchRegionDataFail() {
		try {
			mockMvc.perform(
					get("/api/info/search/region")
					.param("region", "")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().is4xxClientError())
			.andDo(print());
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	@Test
	public void testSearchRegionDataSuccess() throws Exception {
		mockMvc.perform(
				get("/api/info/search/region")
				.param("region", "강릉시")
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	@Test
	public void testModifySupportInfoFail() {
		try {
			ApiParameter param = new ApiParameter();
			param.setTarget("junit Test case");
			mockMvc.perform(
					put("/api/info/modify")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(asJsonString(param))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().is4xxClientError())
			.andDo(print());
		
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	@Test
	public void testModifySupportInfoSuccess() throws Exception {
		ApiParameter param = new ApiParameter();
		param.setRegion("강릉시");
		param.setTarget("junit Test case");
		
		mockMvc.perform(
				put("/api/info/modify")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(asJsonString(param))
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	@Test
	public void testSearchSortAndLimitRegionFail() {
		try {
			mockMvc.perform(
					put("/api/info/sort/region")
					.param("size", "-1")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().is4xxClientError())
			.andDo(print());
			
		} catch (Exception e) {
			e.getMessage();
		}
	}
	
	@Test
	public void testSearchSortAndLimitRegionSuccess() throws Exception {
		mockMvc.perform(
				get("/api/info/sort/region")
				.param("size", "5")
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(status().isOk())
		.andDo(print());
	}
	
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
