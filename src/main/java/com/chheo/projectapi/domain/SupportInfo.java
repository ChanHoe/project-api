package com.chheo.projectapi.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SupportInfo extends TimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 500, nullable = false)
	private String target;
	
	@Column(length = 20, nullable = false)
	private String usage;
	
	@Column(length = 10, nullable = false)
	private String limited;
	
	@Column
	private long limitValue;
	
	@Column(length = 10, nullable = false)
	private String rate;
	
	@Column
	private double averageRate;
	
	@Column(length = 50, nullable = false)
	private String institute;
	
	@Column(length = 50, nullable = false)
	private String mgmt;
	
	@Column(length = 50, nullable = false)
	private String reception;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "region_code")
	private Organ organ;
}
