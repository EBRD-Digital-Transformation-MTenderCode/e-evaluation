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
    CONTRACT_SCHEDULE("contractSchedule"),
    SUBMISSION_DOCUMENTS("submissionDocuments");

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
    EMPTY("empty"),
    AWAITING("awaiting"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    LOT_CANCELLED("lotCancelled");

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

enum class AwardCriteriaDetails(@JsonValue val value: String) {

    AUTOMATED("automated"),
    MANUAL("manual");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, AwardCriteriaDetails> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): AwardCriteriaDetails = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AwardCriteriaDetails::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
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

enum class BusinessFunctionType(@JsonValue val value: String) {
    AUTHORITY("authority"),
    PROCUREMENT_OFFICER("procurementOfficer"),
    CONTRACT_POINT("contactPoint"),
    TECHNICAL_EVALUATOR("technicalEvaluator"),
    TECHNICAL_OPENER("technicalOpener"),
    PRICE_OPENER("priceOpener"),
    PRICE_EVALUATOR("priceEvaluator");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, BusinessFunctionType> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): BusinessFunctionType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = BusinessFunctionType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}

enum class SupplierType(@JsonValue val value: String) {
    COMPANY("company"),
    INDIVIDUAL("individual");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, SupplierType> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): SupplierType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = SupplierType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}

enum class BidStatusType(@JsonValue val value: String) {
    PENDING("pending"),
    DISQUALIFIED("disqualified"),
    VALID("valid"),
    WITHDRAWN("withdrawn"),
    INVITED("invited");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, BidStatusType> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): BidStatusType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = BidStatusType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}

enum class BidStatusDetailsType(@JsonValue val value: String) {
    PENDING("pending"),
    DISQUALIFIED("disqualified"),
    VALID("valid"),
    WITHDRAWN("withdrawn"),
    INVITED("invited"),
    EMPTY("empty");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, BidStatusDetailsType> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): BidStatusDetailsType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = BidStatusDetailsType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}

enum class BidDocumentType(@JsonValue val value: String) {

    SUBMISSION_DOCUMENTS("submissionDocuments"),
    ILLUSTRATION("illustration"),
    X_COMMERCIAL_OFFER("x_commercialOffer"),
    X_QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    X_ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    X_TECHNICAL_DOCUMENTS("x_technicalDocuments");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, BidDocumentType> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): BidDocumentType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = BidDocumentType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}

enum class ConversionsRelatesTo(@JsonValue val value: String) {
    REQUIREMENT("requirement");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, ConversionsRelatesTo> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): ConversionsRelatesTo = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = ConversionsRelatesTo::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
