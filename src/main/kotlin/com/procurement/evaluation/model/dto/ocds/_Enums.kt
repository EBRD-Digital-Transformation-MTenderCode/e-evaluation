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
        private val CONSTANTS: Map<String, AwardStatus> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): AwardStatus = CONSTANTS[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AwardStatus::class.java.name,
                value = value,
                values = values().toString()
            )
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

    companion object {
        private val CONSTANTS: Map<String, AwardStatusDetails> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): AwardStatusDetails = CONSTANTS[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AwardStatusDetails::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}

enum class AwardCriteria(@JsonValue val value: String) {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria");

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
    NEGOTIATION("negotiation"),
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
