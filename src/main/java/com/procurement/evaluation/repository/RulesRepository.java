package com.procurement.evaluation.repository;
import com.procurement.evaluation.model.entity.RulesEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RulesRepository extends CassandraRepository<RulesEntity, String> {

    @Query(value = "select * from evaluation_rules where country=?0 AND method=?1 AND parameter=?2 LIMIT 1")
    RulesEntity getRule(String country, String method, String parameter);

    @Query(value = "select value from evaluation_rules where country=?0 AND method=?1 AND parameter=?2 LIMIT 1")
    String getValue(String country, String method, String parameter);
}
