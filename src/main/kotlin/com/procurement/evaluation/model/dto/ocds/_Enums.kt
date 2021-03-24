package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider

enum class DocumentType(@JsonValue override val key: String) : EnumElementProvider.Key {
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

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentType.orThrow(name)
    }
}

enum class AwardStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardStatus>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardStatus.orThrow(name)
    }
}

enum class AwardStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    ACTIVE("active"),
    AWAITING("awaiting"),
    BASED_ON_HUMAN_DECISION("basedOnHumanDecision"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    LACK_OF_QUALIFICATIONS("lackOfQualifications"),
    LACK_OF_SUBMISSIONS("lackOfSubmissions"),
    LOT_CANCELLED("lotCancelled"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    PENDING("pending"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardStatusDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardStatusDetails.orThrow(name)
    }
}

enum class AwardCriteria(@JsonValue override val key: String) : EnumElementProvider.Key {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteria>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardCriteria.orThrow(name)
    }
}

enum class AwardCriteriaDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    AUTOMATED("automated"),
    MANUAL("manual");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteriaDetails>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardCriteriaDetails.orThrow(name)
    }
}

enum class Phase(@JsonValue override val key: String) : EnumElementProvider.Key {
    AWARDING("awarding"),
    TENDERING("tendering"),
    CLARIFICATION("clarification"),
    NEGOTIATION("negotiation"),
    EMPTY("empty");

    override fun toString(): String = key

    companion object : EnumElementProvider<Phase>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Phase.orThrow(name)
    }
}

enum class BusinessFunctionType(@JsonValue override val key: String) : EnumElementProvider.Key {
    CHAIRMAN("chairman"),
    AUTHORITY("authority"),
    PROCUREMENT_OFFICER("procurementOfficer"),
    CONTRACT_POINT("contactPoint"),
    TECHNICAL_EVALUATOR("technicalEvaluator"),
    TECHNICAL_OPENER("technicalOpener"),
    PRICE_OPENER("priceOpener"),
    PRICE_EVALUATOR("priceEvaluator");

    override fun toString(): String = key

    companion object : EnumElementProvider<BusinessFunctionType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = BusinessFunctionType.orThrow(name)
    }
}

enum class TypeOfSupplier(@JsonValue override val key: String) : EnumElementProvider.Key {
    COMPANY("company"),
    INDIVIDUAL("individual");

    override fun toString(): String = key

    companion object : EnumElementProvider<TypeOfSupplier>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TypeOfSupplier.orThrow(name)
    }
}

enum class BidStatusType(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    DISQUALIFIED("disqualified"),
    VALID("valid"),
    WITHDRAWN("withdrawn"),
    INVITED("invited");

    override fun toString(): String = key

    companion object : EnumElementProvider<BidStatusType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = BidStatusType.orThrow(name)
    }
}

enum class BidStatusDetailsType(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    DISQUALIFIED("disqualified"),
    VALID("valid"),
    WITHDRAWN("withdrawn"),
    INVITED("invited"),
    EMPTY("empty");

    override fun toString(): String = key

    companion object : EnumElementProvider<BidStatusDetailsType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = BidStatusDetailsType.orThrow(name)
    }
}

enum class BidDocumentType(@JsonValue override val key: String) : EnumElementProvider.Key {

    SUBMISSION_DOCUMENTS("submissionDocuments"),
    ILLUSTRATION("illustration"),
    X_COMMERCIAL_OFFER("x_commercialOffer"),
    X_QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    X_ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    X_TECHNICAL_DOCUMENTS("x_technicalDocuments");

    override fun toString(): String = key

    companion object : EnumElementProvider<BidDocumentType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = BidDocumentType.orThrow(name)
    }
}

enum class ConversionsRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {
    REQUIREMENT("requirement");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConversionsRelatesTo>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConversionsRelatesTo.orThrow(name)
    }
}
