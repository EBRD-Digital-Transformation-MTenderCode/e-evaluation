package com.procurement.evaluation.repository;

import com.procurement.evaluation.model.entity.AwardEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository extends CassandraRepository<AwardEntity, String> {

    @Query(value = "select * from evaluation_award where cp_id=?0 AND stage=?1 AND token_entity=?2 LIMIT 1")
    AwardEntity getByCpIdAndStageAndToken(String ocId, String stage, UUID token);

    @Query(value = "select * from evaluation_award where cp_id=?0 AND stage=?1")
    List<AwardEntity> getAllByCpidAndStage(String cpId, String stage);
}
