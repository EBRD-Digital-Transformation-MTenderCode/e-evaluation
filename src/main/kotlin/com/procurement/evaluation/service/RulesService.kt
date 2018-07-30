package com.procurement.evaluation.service

import com.procurement.evaluation.dao.RulesDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import org.springframework.stereotype.Service

interface RulesService {

    fun getRulesMinBids(country: String, method: String): Int
}

@Service
class RulesServiceImpl(private val rulesDao: RulesDao) : RulesService {


    override fun getRulesMinBids(country: String, method: String): Int {
        return rulesDao.getValue(country, method, PARAMETER_MIN_BIDS)?.toIntOrNull()
                ?: throw ErrorException(ErrorType.BIDS_RULES_NOT_FOUND)
    }

    companion object {
        private const val PARAMETER_MIN_BIDS = "minBids"
    }
}
