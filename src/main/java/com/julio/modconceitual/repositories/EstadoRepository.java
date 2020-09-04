package com.julio.modconceitual.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.julio.modconceitual.domain.Estado;
import com.sun.xml.bind.v2.model.core.ID;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {
}
