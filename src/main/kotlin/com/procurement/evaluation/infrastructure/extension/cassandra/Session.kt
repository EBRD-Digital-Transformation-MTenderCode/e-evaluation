package com.procurement.evaluation.infrastructure.extension.cassandra

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.Result.Companion.success

fun BoundStatement.tryExecute(session: Session): Result<ResultSet, Failure.Incident.Database.DatabaseInteractionIncident> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(Failure.Incident.Database.DatabaseInteractionIncident(exception = expected))
}

fun BatchStatement.tryExecute(session: Session): Result<ResultSet, Failure.Incident.Database.DatabaseInteractionIncident> =
    try {
        success(session.execute(this))
    } catch (expected: Exception) {
        failure(Failure.Incident.Database.DatabaseInteractionIncident(exception = expected))
    }

