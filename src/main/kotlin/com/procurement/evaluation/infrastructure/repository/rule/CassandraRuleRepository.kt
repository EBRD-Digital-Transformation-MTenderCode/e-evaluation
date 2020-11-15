package com.procurement.evaluation.infrastructure.repository.rule

import com.datastax.driver.core.Session
import com.procurement.evaluation.application.repository.rule.RuleRepository
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraRuleRepository(private val session: Session) : RuleRepository {

    companion object {

        private const val ALL_OPERATION_TYPE = "all"

        private const val GET_VALUE_CQL = """
               SELECT ${Database.Rules.VALUE}
                 FROM ${Database.KEYSPACE}.${Database.Rules.TABLE_NAME}
                WHERE ${Database.Rules.COUNTRY}=? 
                  AND ${Database.Rules.PMD}=?
                  AND ${Database.Rules.OPERATION_TYPE}=?
                  AND ${Database.Rules.PARAMETER}=?
            """
    }

    private val preparedGetValueByCQL = session.prepare(GET_VALUE_CQL)

    override fun find(
        country: String,
        pmd: ProcurementMethod,
        operationType: OperationType?,
        parameter: String
    ): Result<String?, Failure.Incident.Database> =
        preparedGetValueByCQL.bind()
            .apply {
                setString(Database.Rules.COUNTRY, country)
                setString(Database.Rules.PMD, pmd.name)
                setString(Database.Rules.OPERATION_TYPE, operationType?.key ?: ALL_OPERATION_TYPE)
                setString(Database.Rules.PARAMETER, parameter)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.Rules.VALUE)
            .asSuccess()
}
