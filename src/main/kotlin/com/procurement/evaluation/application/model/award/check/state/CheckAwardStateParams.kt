package com.procurement.evaluation.application.model.award.check.state

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseEnum
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.ProcurementMethodDetails
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.toSetBy

class CheckAwardStateParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType2,
    val awards: List<Award>,
    val tender: Tender?
) {
    companion object {

        val allowedOperationTypes = OperationType2.allowedElements
            .filter {
                when (it) {
                    OperationType2.AWARD_CONSIDERATION,
                    OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
                    OperationType2.PCR_PROTOCOL,
                    OperationType2.UPDATE_AWARD -> true

                    OperationType2.APPLY_QUALIFICATION_PROTOCOL,
                    OperationType2.CREATE_AWARD,
                    OperationType2.CREATE_PCR,
                    OperationType2.CREATE_SUBMISSION,
                    OperationType2.LOT_CANCELLATION,
                    OperationType2.SUBMISSION_PERIOD_END,
                    OperationType2.TENDER_CANCELLATION,
                    OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
                    OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> false

                }
            }
            .toSetBy { it }

        private val allowedPmd = ProcurementMethodDetails.allowedElements
            .filter {
                when (it) {
                    ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
                    ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
                    ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
                    ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
                    ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP,
                    ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
                    ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF -> true

                    ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
                    ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
                    ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
                    ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
                    ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV,
                    ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
                    ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP -> false
                }
            }
            .toSetBy { it }

        fun tryCreate(
            cpid: String,
            ocid: String,
            pmd: String,
            country: String,
            operationType: String,
            awards: List<Award>,
            tender: Tender?
        ): Result<CheckAwardStateParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .onFailure { return it }

            val ocidParsed = parseOcid(ocid)
                .onFailure { return it }

            val parsedOperationType = parseEnum(
                value = operationType,
                target = OperationType2,
                allowedEnums = allowedOperationTypes,
                attributeName = "operationType"
            ).onFailure { return it }

            val parsedPmd = parseEnum(
                value = pmd,
                target = ProcurementMethodDetails,
                allowedEnums = allowedPmd,
                attributeName = "pmd"
            ).onFailure { return it }

            return CheckAwardStateParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                country = country,
                operationType = parsedOperationType,
                pmd = parsedPmd,
                awards = awards,
                tender = tender
            ).asSuccess()
        }
    }

    data class Award(
        val id: String
    )

    data class Tender(
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: String
        )
    }
}
