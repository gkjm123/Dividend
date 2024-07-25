package com.example.dividend.persist;

import com.example.dividend.persist.entity.DividendEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {

  List<DividendEntity> findAllByCompanyId(Long companyId);

  boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);

  void deleteAllByCompanyId(Long id);
}