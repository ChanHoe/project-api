package com.chheo.projectapi.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


import lombok.Getter;

@Getter
@Entity
public class Organ {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 20, nullable = false)
	private String region;
	
	@Column(length = 20, nullable = false)
	private String regionCode;
	
	@OneToMany(mappedBy = "organ", cascade = CascadeType.ALL)
	private List<SupportInfo> supportInfoList = new ArrayList<>();
	
	protected Organ() {}
	
	public Organ(String region, String regionCode) { 
		this.region = region;
		this.regionCode = regionCode; 
	}
}
