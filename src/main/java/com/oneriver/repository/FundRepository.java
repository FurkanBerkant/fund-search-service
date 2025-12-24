package com.oneriver.repository;

import com.oneriver.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FundRepository extends JpaRepository<Fund, Long> {

    List<Fund> findAllByFundCodeIn(List<String> fundCodes);

    Optional<Fund> findByFundCode(String fundCode);
}