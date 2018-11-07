package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException


enum class DocumentType(@JsonValue private val value: String) {
    AWARD_NOTICE("awardNotice"),
    EVALUATION_REPORTS("evaluationReports"),
    SHORTLISTED_FIRMS("shortlistedFirms"),
    WINNING_BID("winningBid"),
    COMPLAINTS("complaints"),
    BIDDERS("bidders"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    CANCELLATION_DETAILS("cancellationDetails"),
    CONTRACT_DRAFT("contractDraft"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_SCHEDULE("contractSchedule");

    override fun toString(): String {
        return this.value
    }
}

enum class AwardStatus(@JsonValue val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, AwardStatus>()

        init {
            AwardStatus.values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): AwardStatus {
            return CONSTANTS[v] ?: throw EnumException(AwardStatus::class.java.name, v, AwardStatus.values().toString())
        }
    }
}

enum class AwardStatusDetails(@JsonValue val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }
}

enum class AwardCriteria(@JsonValue val value: String) {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria"),
    LOWEST_COST("lowestCost"),
    BEST_PROPOSAL("bestProposal"),
    BEST_VALUE_TO_GOVERNMENT("bestValueToGovernment"),
    SINGLE_BID_ONLY("singleBidOnly");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, AwardCriteria>()

        init {
            values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): AwardCriteria {
            return CONSTANTS[v] ?: throw EnumException(AwardCriteria::class.java.name, v, values().toString())
        }
    }
}

enum class Phase(@JsonValue val value: String) {
    AWARDING("awarding"),
    TENDERING("tendering"),
    CLARIFICATION("clarification"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, Phase>()

        init {
            Phase.values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): Phase {
            return CONSTANTS[v] ?: throw EnumException(Phase::class.java.name, v, values().toString())
        }
    }
}
