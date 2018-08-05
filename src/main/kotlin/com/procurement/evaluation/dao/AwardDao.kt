package com.procurement.evaluation.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.model.entity.AwardEntity
import org.springframework.stereotype.Service
import java.util.*

interface AwardDao {

    fun save(entity: AwardEntity)

    fun saveAll(entities: List<AwardEntity>)

    fun findAllByCpIdAndStage(cpId: String, stage: String): List<AwardEntity>

    fun getByCpIdAndStageAndToken(cpId: String, stage: String, token: UUID): AwardEntity

}

@Service
class AwardDaoImpl(private val session: Session) : AwardDao {

    override fun save(entity: AwardEntity) {
        val insert =
                QueryBuilder.insertInto(AWARD_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(TOKEN, entity.token)
                        .value(STAGE, entity.stage)
                        .value(OWNER, entity.owner)
                        .value(STATUS, entity.status)
                        .value(STATUS_DETAILS, entity.statusDetails)
                        .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
    }


    override fun saveAll(entities: List<AwardEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(QueryBuilder.insertInto(AWARD_TABLE)
                    .value(CP_ID, entity.cpId)
                    .value(TOKEN, entity.token)
                    .value(STAGE, entity.stage)
                    .value(OWNER, entity.owner)
                    .value(STATUS, entity.status)
                    .value(STATUS_DETAILS, entity.statusDetails)
                    .value(JSON_DATA, entity.jsonData)
            )
        }
        val batch = QueryBuilder.batch(*operations.toTypedArray())
        session.execute(batch)
    }

    override fun findAllByCpIdAndStage(cpId: String, stage: String): List<AwardEntity> {
        val query = select()
                .all()
                .from(AWARD_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(STAGE, stage))
        val resultSet = session.execute(query)
        val entities = ArrayList<AwardEntity>()
        resultSet.forEach { row ->
            entities.add(
                    AwardEntity(
                            cpId = row.getString(CP_ID),
                            token = row.getUUID(TOKEN),
                            stage = row.getString(STAGE),
                            owner = row.getString(OWNER),
                            status = row.getString(STATUS),
                            statusDetails = row.getString(STATUS_DETAILS),
                            jsonData = row.getString(JSON_DATA)))
        }
        if (entities.isEmpty()) throw ErrorException(ErrorType.DATA_NOT_FOUND)
        return entities
    }

    override fun getByCpIdAndStageAndToken(cpId: String, stage: String, token: UUID): AwardEntity {
        val query = select()
                .all()
                .from(AWARD_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(STAGE, stage))
                .and(eq(TOKEN, token))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            AwardEntity(
                    cpId = row.getString(CP_ID),
                    token = row.getUUID(TOKEN),
                    stage = row.getString(STAGE),
                    owner = row.getString(OWNER),
                    status = row.getString(STATUS),
                    statusDetails = row.getString(STATUS_DETAILS),
                    jsonData = row.getString(JSON_DATA))
        else throw ErrorException(ErrorType.DATA_NOT_FOUND)
    }

    companion object {
        private val AWARD_TABLE = "evaluation_award"
        private val CP_ID = "cp_id"
        private val STAGE = "stage"
        private val TOKEN = "token_entity"
        private val OWNER = "owner"
        private val STATUS = "status"
        private val STATUS_DETAILS = "status_details"
        private val JSON_DATA = "json_data"
    }
}
