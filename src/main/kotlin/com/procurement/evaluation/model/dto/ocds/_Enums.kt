package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException
import java.util.*

enum class Currency constructor(private val value: String) {

    ADP("ADP"),
    AED("AED"),
    AFA("AFA"),
    AFN("AFN"),
    ALK("ALK"),
    ALL("ALL"),
    AMD("AMD"),
    ANG("ANG"),
    AOA("AOA"),
    AOK("AOK"),
    AON("AON"),
    AOR("AOR"),
    ARA("ARA"),
    ARP("ARP"),
    ARS("ARS"),
    ARY("ARY"),
    ATS("ATS"),
    AUD("AUD"),
    AWG("AWG"),
    AYM("AYM"),
    AZM("AZM"),
    AZN("AZN"),
    BAD("BAD"),
    BAM("BAM"),
    BBD("BBD"),
    BDT("BDT"),
    BEC("BEC"),
    BEF("BEF"),
    BEL("BEL"),
    BGJ("BGJ"),
    BGK("BGK"),
    BGL("BGL"),
    BGN("BGN"),
    BHD("BHD"),
    BIF("BIF"),
    BMD("BMD"),
    BND("BND"),
    BOB("BOB"),
    BOP("BOP"),
    BOV("BOV"),
    BRB("BRB"),
    BRC("BRC"),
    BRE("BRE"),
    BRL("BRL"),
    BRN("BRN"),
    BRR("BRR"),
    BSD("BSD"),
    BTN("BTN"),
    BUK("BUK"),
    BWP("BWP"),
    BYB("BYB"),
    BYN("BYN"),
    BYR("BYR"),
    BZD("BZD"),
    CAD("CAD"),
    CDF("CDF"),
    CHC("CHC"),
    CHE("CHE"),
    CHF("CHF"),
    CHW("CHW"),
    CLF("CLF"),
    CLP("CLP"),
    CNY("CNY"),
    COP("COP"),
    COU("COU"),
    CRC("CRC"),
    CSD("CSD"),
    CSJ("CSJ"),
    CSK("CSK"),
    CUC("CUC"),
    CUP("CUP"),
    CVE("CVE"),
    CYP("CYP"),
    CZK("CZK"),
    DDM("DDM"),
    DEM("DEM"),
    DJF("DJF"),
    DKK("DKK"),
    DOP("DOP"),
    DZD("DZD"),
    ECS("ECS"),
    ECV("ECV"),
    EEK("EEK"),
    EGP("EGP"),
    ERN("ERN"),
    ESA("ESA"),
    ESB("ESB"),
    ESP("ESP"),
    ETB("ETB"),
    EUR("EUR"),
    FIM("FIM"),
    FJD("FJD"),
    FKP("FKP"),
    FRF("FRF"),
    GBP("GBP"),
    GEK("GEK"),
    GEL("GEL"),
    GHC("GHC"),
    GHP("GHP"),
    GHS("GHS"),
    GIP("GIP"),
    GMD("GMD"),
    GNE("GNE"),
    GNF("GNF"),
    GNS("GNS"),
    GQE("GQE"),
    GRD("GRD"),
    GTQ("GTQ"),
    GWE("GWE"),
    GWP("GWP"),
    GYD("GYD"),
    HKD("HKD"),
    HNL("HNL"),
    HRD("HRD"),
    HRK("HRK"),
    HTG("HTG"),
    HUF("HUF"),
    IDR("IDR"),
    IEP("IEP"),
    ILP("ILP"),
    ILR("ILR"),
    ILS("ILS"),
    INR("INR"),
    IQD("IQD"),
    IRR("IRR"),
    ISJ("ISJ"),
    ISK("ISK"),
    ITL("ITL"),
    JMD("JMD"),
    JOD("JOD"),
    JPY("JPY"),
    KES("KES"),
    KGS("KGS"),
    KHR("KHR"),
    KMF("KMF"),
    KPW("KPW"),
    KRW("KRW"),
    KWD("KWD"),
    KYD("KYD"),
    KZT("KZT"),
    LAJ("LAJ"),
    LAK("LAK"),
    LBP("LBP"),
    LKR("LKR"),
    LRD("LRD"),
    LSL("LSL"),
    LSM("LSM"),
    LTL("LTL"),
    LTT("LTT"),
    LUC("LUC"),
    LUF("LUF"),
    LUL("LUL"),
    LVL("LVL"),
    LVR("LVR"),
    LYD("LYD"),
    MAD("MAD"),
    MDL("MDL"),
    MGA("MGA"),
    MGF("MGF"),
    MKD("MKD"),
    MLF("MLF"),
    MMK("MMK"),
    MNT("MNT"),
    MOP("MOP"),
    MRO("MRO"),
    MTL("MTL"),
    MTP("MTP"),
    MUR("MUR"),
    MVQ("MVQ"),
    MVR("MVR"),
    MWK("MWK"),
    MXN("MXN"),
    MXP("MXP"),
    MXV("MXV"),
    MYR("MYR"),
    MZE("MZE"),
    MZM("MZM"),
    MZN("MZN"),
    NAD("NAD"),
    NGN("NGN"),
    NIC("NIC"),
    NIO("NIO"),
    NLG("NLG"),
    NOK("NOK"),
    NPR("NPR"),
    NZD("NZD"),
    OMR("OMR"),
    PAB("PAB"),
    PEH("PEH"),
    PEI("PEI"),
    PEN("PEN"),
    PES("PES"),
    PGK("PGK"),
    PHP("PHP"),
    PKR("PKR"),
    PLN("PLN"),
    PLZ("PLZ"),
    PTE("PTE"),
    PYG("PYG"),
    QAR("QAR"),
    RHD("RHD"),
    ROK("ROK"),
    ROL("ROL"),
    RON("RON"),
    RSD("RSD"),
    RUB("RUB"),
    RUR("RUR"),
    RWF("RWF"),
    SAR("SAR"),
    SBD("SBD"),
    SCR("SCR"),
    SDD("SDD"),
    SDG("SDG"),
    SDP("SDP"),
    SEK("SEK"),
    SGD("SGD"),
    SHP("SHP"),
    SIT("SIT"),
    SKK("SKK"),
    SLL("SLL"),
    SOS("SOS"),
    SRD("SRD"),
    SRG("SRG"),
    SSP("SSP"),
    STD("STD"),
    SUR("SUR"),
    SVC("SVC"),
    SYP("SYP"),
    SZL("SZL"),
    THB("THB"),
    TJR("TJR"),
    TJS("TJS"),
    TMM("TMM"),
    TMT("TMT"),
    TND("TND"),
    TOP("TOP"),
    TPE("TPE"),
    TRL("TRL"),
    TRY("TRY"),
    TTD("TTD"),
    TWD("TWD"),
    TZS("TZS"),
    UAH("UAH"),
    UAK("UAK"),
    UGS("UGS"),
    UGW("UGW"),
    UGX("UGX"),
    USD("USD"),
    USN("USN"),
    USS("USS"),
    UYI("UYI"),
    UYN("UYN"),
    UYP("UYP"),
    UYU("UYU"),
    UZS("UZS"),
    VEB("VEB"),
    VEF("VEF"),
    VNC("VNC"),
    VND("VND"),
    VUV("VUV"),
    WST("WST"),
    XAF("XAF"),
    XAG("XAG"),
    XAU("XAU"),
    XBA("XBA"),
    XBB("XBB"),
    XBC("XBC"),
    XBD("XBD"),
    XCD("XCD"),
    XDR("XDR"),
    XEU("XEU"),
    XFO("XFO"),
    XFU("XFU"),
    XOF("XOF"),
    XPD("XPD"),
    XPF("XPF"),
    XPT("XPT"),
    XRE("XRE"),
    XSU("XSU"),
    XTS("XTS"),
    XUA("XUA"),
    XXX("XXX"),
    YDD("YDD"),
    YER("YER"),
    YUD("YUD"),
    YUM("YUM"),
    YUN("YUN"),
    ZAL("ZAL"),
    ZAR("ZAR"),
    ZMK("ZMK"),
    ZMW("ZMW"),
    ZRN("ZRN"),
    ZRZ("ZRZ"),
    ZWC("ZWC"),
    ZWD("ZWD"),
    ZWL("ZWL"),
    ZWN("ZWN"),
    ZWR("ZWR");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, Currency>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): Currency {
            return CONSTANTS[value]
                    ?: throw EnumException(Currency::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class DocumentType constructor(private val value: String) {
    TENDER_NOTICE("tenderNotice"),
    AWARD_NOTICE("awardNotice"),
    CONTRACT_NOTICE("contractNotice"),
    COMPLETION_CERTIFICATE("completionCertificate"),
    PROCUREMENT_PLAN("procurementPlan"),
    BIDDING_DOCUMENTS("biddingDocuments"),
    TECHNICAL_SPECIFICATIONS("technicalSpecifications"),
    EVALUATION_CRITERIA("evaluationCriteria"),
    EVALUATION_REPORTS("evaluationReports"),
    CONTRACT_DRAFT("contractDraft"),
    CONTRACT_SIGNED("contractSigned"),
    CONTRACT_ARRANGEMENTS("contractArrangements"),
    CONTRACT_SCHEDULE("contractSchedule"),
    PHYSICAL_PROGRESS_REPORT("physicalProgressReport"),
    FINANCIAL_PROGRESS_REPORT("financialProgressReport"),
    FINAL_AUDIT("finalAudit"),
    HEARING_NOTICE("hearingNotice"),
    MARKET_STUDIES("marketStudies"),
    ELIGIBILITY_CRITERIA("eligibilityCriteria"),
    CLARIFICATIONS("clarifications"),
    SHORTLISTED_FIRMS("shortlistedFirms"),
    ENVIRONMENTAL_IMPACT("environmentalImpact"),
    ASSET_AND_LIABILITY_ASSESSMENT("assetAndLiabilityAssessment"),
    RISK_PROVISIONS("riskProvisions"),
    WINNING_BID("winningBid"),
    COMPLAINTS("complaints"),
    CONTRACT_ANNEXE("contractAnnexe"),
    CONTRACT_GUARANTEES("contractGuarantees"),
    SUB_CONTRACT("subContract"),
    NEEDS_ASSESSMENT("needsAssessment"),
    FEASIBILITY_STUDY("feasibilityStudy"),
    PROJECT_PLAN("projectPlan"),
    BILL_OF_QUANTITY("billOfQuantity"),
    BIDDERS("bidders"),
    CONFLICT_OF_INTEREST("conflictOfInterest"),
    DEBARMENTS("debarments"),
    ILLUSTRATION("illustration"),
    SUBMISSION_DOCUMENTS("submissionDocuments"),
    CONTRACT_SUMMARY("contractSummary"),
    CANCELLATION_DETAILS("cancellationDetails");

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
