package com.procurement.evaluation.repository;

import com.procurement.evaluation.model.entity.RulesEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RulesRepository extends CassandraRepository<RulesEntity, String> {

    @Query(value = "select value from evaluation_rules where country=?0 AND pmd=?1 AND parameter=?2 LIMIT 1")
    String getValue(String country, String pmd, String parameter);
}
