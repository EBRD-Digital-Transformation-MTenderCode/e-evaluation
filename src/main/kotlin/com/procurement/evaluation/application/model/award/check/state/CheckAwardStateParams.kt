package com.procurement.evaluation.application.model.award.check.state

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseEnum
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.toSetBy

class CheckAwardStateParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethod,
    val country: String,
    val operationType: OperationType2
) {
    companion object {

        private val allowedOperationTypes = OperationType2.allowedElements
            .filter {
                when (it) {
                    OperationType2.UPDATE_AWARD -> true

                    OperationType2.APPLY_QUALIFICATION_PROTOCOL,
                    OperationType2.CREATE_AWARD,
                    OperationType2.CREATE_PCR,
                    OperationType2.CREATE_SUBMISSION,
                    OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
                    OperationType2.LOT_CANCELLATION,
                    OperationType2.SUBMISSION_PERIOD_END,
                    OperationType2.TENDER_CANCELLATION,
                    OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
                    OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> false

                }
            }
            .toSetBy { it }

        private val allowedPmd = ProcurementMethod.allowedElements
            .filter {
                when (it) {
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP -> true

                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> false
                }
            }
            .toSetBy { it }

        fun tryCreate(
            cpid: String,
            ocid: String,
            pmd: String,
            country: String,
            operationType: String
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
                target = ProcurementMethod,
                allowedEnums = allowedPmd,
                attributeName = "pmd"
            ).onFailure { return it }

            return CheckAwardStateParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                country = country,
                operationType = parsedOperationType,
                pmd = parsedPmd
            ).asSuccess()
        }
    }
}
