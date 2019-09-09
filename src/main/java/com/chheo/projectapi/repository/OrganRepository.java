package com.chheo.projectapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chheo.projectapi.domain.Organ;

@Repository
public interface OrganRepository extends JpaRepository<Organ, Long> {
	
}
