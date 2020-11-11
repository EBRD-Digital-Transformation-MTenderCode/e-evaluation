package com.procurement.evaluation.infrastructure.repository.rule

import com.datastax.driver.core.Session
import com.procurement.evaluation.application.repository.rule.RuleRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.repository.Database
import org.springframework.stereotype.Repository

@Repository
class CassandraRuleRepository(private val session: Session) : RuleRepository {

    companion object {

        private const val GET_VALUE_CQL = """
               SELECT ${Database.Rules.VALUE}
                 FROM ${Database.KEYSPACE}.${Database.Rules.TABLE_NAME}
                WHERE ${Database.Rules.COUNTRY}=? 
                  AND ${Database.Rules.PMD}=?
                  AND ${Database.Rules.PARAMETER}=?
            """
    }

    private val preparedGetValueByCQL = session.prepare(GET_VALUE_CQL)

    override fun find(
        country: String,
        pmd: ProcurementMethod,
        parameter: String
    ): Result<String?, Fail.Incident.Database.DatabaseInteractionIncident> =
        preparedGetValueByCQL.bind()
            .apply {
                setString(Database.Rules.COUNTRY, country)
                setString(Database.Rules.PMD, pmd.name)
                setString(Database.Rules.PARAMETER, parameter)
            }
            .tryExecute(session)
            .orForwardFail { return it }
            .one()
            ?.getString(Database.Rules.VALUE)
            .asSuccess()
}
