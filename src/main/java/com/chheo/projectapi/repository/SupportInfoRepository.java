package com.chheo.projectapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chheo.projectapi.domain.SupportInfo;


@Repository
public interface SupportInfoRepository extends JpaRepository<SupportInfo, Long> {
	
}
