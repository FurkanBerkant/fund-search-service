package com.oneriver.repository;

import com.oneriver.entity.Fund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FundRepository extends JpaRepository<Fund, Long> {
    List<Fund> findAllByFundCodeIn(List<String> fundCodes);
    Slice<Fund> findAllBy(Pageable pageable);
}