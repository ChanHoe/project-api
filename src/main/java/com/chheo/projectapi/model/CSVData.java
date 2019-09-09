package com.chheo.projectapi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CSVData {
	private String division;
	private String region;
	private String target;
	private String usage;
	private String limit;
	private String rate;
	private String institute;
	private String mgmt;
	private String reception;
}
