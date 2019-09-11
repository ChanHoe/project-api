package com.chheo.projectapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chheo.projectapi.domain.Organ;

@Repository
public interface OrganRepository extends JpaRepository<Organ, Long> {
	
	/**
	 * 지자체명으로 지원기간 정보 select
	 * @param region
	 * @return
	 */
	public Organ findByRegion(String region);
}
