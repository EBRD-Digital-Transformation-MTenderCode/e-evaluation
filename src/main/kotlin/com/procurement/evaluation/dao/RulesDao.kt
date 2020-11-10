package com.procurement.evaluation.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.evaluation.infrastructure.repository.Database
import org.springframework.stereotype.Service

@Service
class RulesDao(private val session: Session) {

    fun getValue(country: String, pmd: String, parameter: String): String? {
        val query = select()
                .column(Database.Rules.VALUE)
                .from(Database.KEYSPACE, Database.Rules.TABLE)
                .where(eq(Database.Rules.COUNTRY, country))
                .and(eq(Database.Rules.PMD, pmd))
                .and(eq(Database.Rules.PARAMETER, parameter))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null) return row.getString(Database.Rules.VALUE)
        else null
    }

}
