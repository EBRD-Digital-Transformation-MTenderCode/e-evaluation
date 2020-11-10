package com.procurement.evaluation.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.infrastructure.tools.toLocalDateTime
import com.procurement.evaluation.model.entity.PeriodEntity
import org.springframework.stereotype.Service

@Service
class PeriodDao(private val session: Session) {

    fun save(entity: PeriodEntity) {
        val insert =
            insertInto(Database.KEYSPACE, Database.Period.TABLE)
                .value(Database.Period.CPID, entity.cpid)
                .value(Database.Period.OCID, entity.ocid)
                .value(Database.Period.AWARD_CRITERIA, entity.awardCriteria)
                .value(Database.Period.START_DATE, entity.startDate)
                .value(Database.Period.END_DATE, entity.endDate)
        session.execute(insert)
    }

    fun getByCpIdAndStage(cpid: Cpid, ocid: Ocid): PeriodEntity {
        val query = select()
            .all()
            .from(Database.KEYSPACE, Database.Period.TABLE)
            .where(eq(Database.Period.CPID, cpid))
            .and(eq(Database.Period.OCID, ocid)).limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            PeriodEntity(
                cpid = cpid,
                ocid = ocid,
                awardCriteria = row.getString(Database.Period.AWARD_CRITERIA),
                startDate = row.getTimestamp(Database.Period.START_DATE).toLocalDateTime(),
                endDate = row.getTimestamp(Database.Period.END_DATE).toLocalDateTime()
            )
        else throw ErrorException(ErrorType.PERIOD_NOT_FOUND)
    }
}