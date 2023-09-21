package com.springbatch.ownspringbatch.repository;

import com.springbatch.ownspringbatch.dto.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor,Long> {
}
