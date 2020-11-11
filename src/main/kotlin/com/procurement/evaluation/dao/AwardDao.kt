package com.procurement.evaluation.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.model.entity.AwardEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class AwardDao(private val session: Session) {

    fun save(entity: AwardEntity) {
        val insert =
                QueryBuilder.insertInto(Database.KEYSPACE, Database.Awards.TABLE)
                        .value(Database.Awards.CPID, entity.cpid)
                        .value(Database.Awards.TOKEN_ENTITY, entity.token)
                        .value(Database.Awards.OCID, entity.ocid)
                        .value(Database.Awards.OWNER, entity.owner)
                        .value(Database.Awards.STATUS, entity.status)
                        .value(Database.Awards.STATUS_DETAILS, entity.statusDetails)
                        .value(Database.Awards.JSON_DATA, entity.jsonData)
        session.execute(insert)
    }


    fun saveAll(entities: List<AwardEntity>) {
        entities.forEach { save(it) }
    }

    fun findAllByCpIdAndStage(cpid: Cpid, ocid: Ocid): List<AwardEntity> {
        val query = select()
                .all()
                .from(Database.KEYSPACE, Database.Awards.TABLE)
                .where(eq(Database.Awards.CPID, cpid))
                .and(eq(Database.Awards.OCID, ocid))

        val resultSet = session.execute(query)
        val entities = ArrayList<AwardEntity>()
        resultSet.forEach { row ->
            entities.add(
                    AwardEntity(
                        cpid = Cpid.tryCreateOrNull(row.getString(Database.Awards.CPID))!!,
                        token = row.getUUID(Database.Awards.TOKEN_ENTITY),
                        ocid = Ocid.tryCreateOrNull(row.getString(Database.Awards.OCID))!!,
                        owner = row.getString(Database.Awards.OWNER),
                        status = row.getString(Database.Awards.STATUS),
                        statusDetails = row.getString(Database.Awards.STATUS_DETAILS),
                        jsonData = row.getString(Database.Awards.JSON_DATA)))
        }
        return entities
    }

    fun getByCpIdAndStageAndToken(cpid: Cpid, ocid: Ocid, token: UUID): AwardEntity {
        val query = select()
                .all()
                .from(Database.KEYSPACE, Database.Awards.TABLE)
                .where(eq(Database.Awards.CPID, cpid))
                .and(eq(Database.Awards.OCID, ocid))
                .and(eq(Database.Awards.TOKEN_ENTITY, token))
                .limit(1)

        val row = session.execute(query).one()
        return if (row != null)
            AwardEntity(
                cpid = Cpid.tryCreateOrNull(row.getString(Database.Awards.CPID))!!,
                token = row.getUUID(Database.Awards.TOKEN_ENTITY),
                ocid = Ocid.tryCreateOrNull(row.getString(Database.Awards.OCID))!!,
                owner = row.getString(Database.Awards.OWNER),
                status = row.getString(Database.Awards.STATUS),
                statusDetails = row.getString(Database.Awards.STATUS_DETAILS),
                jsonData = row.getString(Database.Awards.JSON_DATA))
        else throw ErrorException(ErrorType.DATA_NOT_FOUND)
    }

    fun findAllByCpId(cpid: Cpid): List<AwardEntity> {
        val query = select()
                .all()
                .from(Database.KEYSPACE, Database.Awards.TABLE)
                .where(eq(Database.Awards.CPID, cpid))

        val resultSet = session.execute(query)
        val entities = ArrayList<AwardEntity>()
        resultSet.forEach { row ->
            entities.add(
                    AwardEntity(
                        cpid = Cpid.tryCreateOrNull(row.getString(Database.Awards.CPID))!!,
                        token = row.getUUID(Database.Awards.TOKEN_ENTITY),
                        ocid = Ocid.tryCreateOrNull(row.getString(Database.Awards.OCID))!!,
                        owner = row.getString(Database.Awards.OWNER),
                        status = row.getString(Database.Awards.STATUS),
                        statusDetails = row.getString(Database.Awards.STATUS_DETAILS),
                        jsonData = row.getString(Database.Awards.JSON_DATA)))
        }
        return entities
    }

}
