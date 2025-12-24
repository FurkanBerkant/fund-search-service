package com.oneriver.repository;

import com.oneriver.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundRepository extends JpaRepository<Fund, String> {

    @EntityGraph(attributePaths = {"returnPeriods"})
    @Query("SELECT f FROM Fund f WHERE f.fundCode IN :fundCodes")
    List<Fund> findAllByFundCodeIn(List<String> fundCodes);

    @EntityGraph(attributePaths = {"returnPeriods"})
    @Query("SELECT f FROM Fund f")
    @Override
    List<Fund> findAll();
}