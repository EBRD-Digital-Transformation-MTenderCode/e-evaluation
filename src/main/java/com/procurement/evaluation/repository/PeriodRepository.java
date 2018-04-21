package com.procurement.evaluation.repository;

import com.procurement.evaluation.model.entity.PeriodEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends CassandraRepository<PeriodEntity, String> {

    @Query(value = "select * from evaluation_period where cp_id=?0 AND stage=?1 LIMIT 1")
    PeriodEntity getByCpIdAndStage(String cpId, String stage);
}

