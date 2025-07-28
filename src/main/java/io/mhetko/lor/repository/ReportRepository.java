package io.mhetko.lor.repository;

import io.mhetko.lor.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
