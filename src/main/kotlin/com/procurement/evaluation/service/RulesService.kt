package com.procurement.evaluation.service

import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.repository.rule.CassandraRuleRepository
import org.springframework.stereotype.Service

@Service
class RulesService(private val rulesRepository: CassandraRuleRepository) {

    fun getRulesMinBids(country: String, method: ProcurementMethod, operationType: OperationType?): Int {
        return rulesRepository.find(country, method, operationType, PARAMETER_MIN_BIDS)
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

    companion object {
        private const val PARAMETER_MIN_BIDS = "minBids"
    }
}
