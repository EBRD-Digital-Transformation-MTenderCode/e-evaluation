package com.procurement.evaluation.repository;
import com.procurement.evaluation.model.entity.AwardEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository extends CassandraRepository<AwardEntity, String> {

    @Query(value = "select * from evaluation_award where oc_id=?0 AND award_id=?1 LIMIT 1")
    AwardEntity findAwardEntity(String ocId, UUID awardId);

    @Query(value = "select * from evaluation_award where oc_id=?0")
    List<AwardEntity> selectAwardsEntityByOcid(String ocId);
}
