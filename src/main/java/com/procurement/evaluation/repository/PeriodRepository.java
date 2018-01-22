package com.procurement.evaluation.repository;

import com.procurement.evaluation.model.entity.AwardPeriodEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends CassandraRepository<AwardPeriodEntity, String> {

    @Query(value = "select * from evaluation_period where oc_id=?0 LIMIT 1")
    AwardPeriodEntity getByOcId(String ocId);
}

