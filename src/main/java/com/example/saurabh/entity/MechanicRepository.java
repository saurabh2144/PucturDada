package com.example.saurabh.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MechanicRepository extends JpaRepository<Mechanic, Long> {
	
	   List<Mechanic> findByNameContainingIgnoreCase(String name);
	Mechanic findByName(String name);

	List<Mechanic> findByStatus(String string);
}
