package com.chheo.projectapi.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;

@Getter
@Entity
public class SupportInfo extends TimeEntity {
	private static final long serialVersionUID = 1L;
	
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
	
	@ManyToOne
	@JoinColumn(name = "region_code")
	private Organ organ;
	
	protected SupportInfo() {}
	
	public SupportInfo(String target, String usage, String limited, long limitValue, String rate, double averageRate, 
			String institute, String mgmt, String reception, Organ organ) {
		this.target = target;
		this.usage = usage;
		this.limited = limited;
		this.limitValue = limitValue;
		this.rate = rate;
		this.averageRate = averageRate;
		this.institute = institute;
		this.mgmt = mgmt;
		this.reception = reception;
		this.organ = organ;
	} 
}
