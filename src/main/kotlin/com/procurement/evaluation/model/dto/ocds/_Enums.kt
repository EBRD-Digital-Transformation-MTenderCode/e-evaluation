package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException
import java.util.*


enum class DocumentType constructor(private val value: String) {

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

//    TENDER_NOTICE("tenderNotice"),
//    CONTRACT_NOTICE("contractNotice"),
//    COMPLETION_CERTIFICATE("completionCertificate"),
//    PROCUREMENT_PLAN("procurementPlan"),
//    BIDDING_DOCUMENTS("biddingDocuments"),
//    TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
//    EVALUATION_CRITERIA("evaluationCriteria"),
//    PHYSICAL_PROGRESS_REPORT("physicalProgressReport"),
//    FINANCIAL_PROGRESS_REPORT("financialProgressReport"),
//    FINAL_AUDIT("finalAudit"),
//    HEARING_NOTICE("hearingNotice"),
//    MARKET_STUDIES("marketStudies"),
//    ELIGIBILITY_CRITERIA("eligibilityCriteria"),
//    CLARIFICATIONS("clarifications"),
//    ENVIRONMENTAL_IMPACT("environmentalImpact"),
//    ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
//    RISK_PROVISIONS("riskProvisions"),
//    CONTRACT_ANNEXE("contractAnnexe"),
//    CONTRACT_GUARANTEES("contractGuarantees"),
//    SUB_CONTRACT("subContract"),
//    NEEDS_ASSESSMENT("needsAssessment"),
//    FEASIBILITY_STUDY("feasibilityStudy"),
//    PROJECT_PLAN("projectPlan"),
//    BILL_OF_QUANTITY("billOfQuantity"),
//    DEBARMENTS("debarments"),
//    ILLUSTRATION("illustration"),
//    SUBMISSION_DOCUMENTS("submissionDocuments"),
//    CONTRACT_SUMMARY("contractSummary"),
//    CONTRACT_SIGNED("contractSigned"),


    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, DocumentType>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): DocumentType {
            return CONSTANTS[value]
                    ?: throw EnumException(DocumentType::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class Status constructor(private val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, Status>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): Status {
            return CONSTANTS[value] ?: throw IllegalArgumentException(value)
        }
    }

}

enum class AwardCriteria constructor(private val value: String) {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria"),
    LOWEST_COST("lowestCost"),
    BEST_PROPOSAL("bestProposal"),
    BEST_VALUE_TO_GOVERNMENT("bestValueToGovernment"),
    SINGLE_BID_ONLY("singleBidOnly");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, AwardCriteria>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): AwardCriteria {
            return CONSTANTS[value]
                    ?: throw EnumException(AwardCriteria::class.java.name, value, Arrays.toString(values()))
        }
    }
}
