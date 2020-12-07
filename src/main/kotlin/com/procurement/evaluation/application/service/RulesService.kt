package com.procurement.evaluation.application.service

import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.ProcurementMethodDetails
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.domain.model.state.States
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import com.procurement.evaluation.infrastructure.repository.rule.CassandraRuleRepository
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class RulesService(
    private val rulesRepository: CassandraRuleRepository,
    private val transform: Transform
) {

    fun getRulesMinBids(country: String, method: ProcurementMethod, operationType: OperationType?): Int {
        return rulesRepository.find(country, method, operationType?.key, PARAMETER_MIN_BIDS)
            .map { value ->
                value?.toIntOrNull()
                    ?: throw ErrorException(
                        error = ErrorType.INVALID_ATTRIBUTE,
                        message = "Cannot convert $value as integer value."
                    )
            }
            .orThrow {
                throw ErrorException(ErrorType.BIDS_RULES)
            }
    }

    fun findValidStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType2
    ): Result<States, Failure> {

        val states = rulesRepository.find(
            country = country,
            operationType = operationType.key,
            pmd = ProcurementMethod.fromString(pmd.key),
            parameter = VALID_AWARD_STATES_PARAMETER
        )
            .onFailure { fail -> return fail }
            ?: return ValidationError.MissingRule(
                mapOf("country" to country, "pmd" to pmd, "operationType" to operationType)
            ).asFailure()

        return states.convert()
    }

    companion object {
        private const val PARAMETER_MIN_BIDS = "minBids"
        private const val VALID_AWARD_STATES_PARAMETER = "validAwardStates"
    }

    private fun String.convert(): Result<States, Failure.Incident.Transform.ParseFromDatabaseIncident> =
        this.let { value ->
            transform.tryDeserialization(value = value, target = States::class.java)
                .onFailure { fail ->
                    return Failure.Incident.Transform.ParseFromDatabaseIncident(value, exception = fail.reason.exception)
                        .asFailure()
                }
        }.asSuccess()
}
