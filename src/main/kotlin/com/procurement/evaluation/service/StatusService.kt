package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.model.dto.AwardForCansRs
import com.procurement.evaluation.model.dto.AwardsForAcRq
import com.procurement.evaluation.model.dto.AwardsForAcRs
import com.procurement.evaluation.model.dto.EndAwardPeriodRs
import com.procurement.evaluation.model.dto.GetLotForCheckRs
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.cpid
import com.procurement.evaluation.model.dto.bpe.ocid
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.toLocal
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class StatusService(private val periodService: PeriodService,
                    private val awardDao: AwardDao) {

    fun getAwardIdForCheck(cm: CommandMessage): AwardForCansRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val bidId = cm.context.id ?: throw ErrorException(CONTEXT)

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
        val award = awards.asSequence().firstOrNull {
            it.relatedBid == bidId
        }
                ?: throw ErrorException(DATA_NOT_FOUND)

        return AwardForCansRs(award.id)
    }

    fun getAwardsForAc(cm: CommandMessage): AwardsForAcRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val dto = toObject(AwardsForAcRq::class.java, cm.data)
        val awardsIdsSet = dto.cans.asSequence().map { it.awardId }.toSet()

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
                .asSequence()
                .filter { awardsIdsSet.contains(it.id) }
                .toList()
        return AwardsForAcRs(awards)
    }

    fun endAwardPeriod(cm: CommandMessage): EndAwardPeriodRs {
        val cpid = cm.cpid
        val ocid = cm.ocid

        val endDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val awardPeriod = periodService.saveEndOfPeriod(cpid, ocid, endDate)
        return EndAwardPeriodRs(awardPeriod)
    }

    fun getLotForCheck(cm: CommandMessage): GetLotForCheckRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val awardEntity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
        if (awardEntity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val awardByBid = toObject(Award::class.java, awardEntity.jsonData)
        return GetLotForCheckRs(awardByBid.relatedLots[0])
    }

    private fun getAwardsFromEntities(awardEntities: List<AwardEntity>): List<Award> {
        return awardEntities.asSequence().map { toObject(Award::class.java, it.jsonData) }.toList()
    }

    private fun getStage(pmd: ProcurementMethod): String = when (pmd) {
        ProcurementMethod.OT, ProcurementMethod.TEST_OT,
        ProcurementMethod.SV, ProcurementMethod.TEST_SV,
        ProcurementMethod.MV, ProcurementMethod.TEST_MV -> "EV"

        ProcurementMethod.CD, ProcurementMethod.TEST_CD,
        ProcurementMethod.DA, ProcurementMethod.TEST_DA,
        ProcurementMethod.DC, ProcurementMethod.TEST_DC,
        ProcurementMethod.IP, ProcurementMethod.TEST_IP,
        ProcurementMethod.NP, ProcurementMethod.TEST_NP,
        ProcurementMethod.OP, ProcurementMethod.TEST_OP -> "NP"

        ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
        ProcurementMethod.RT, ProcurementMethod.TEST_RT -> "TP"

        ProcurementMethod.CF, ProcurementMethod.TEST_CF, 
        ProcurementMethod.FA, ProcurementMethod.TEST_FA, 
        ProcurementMethod.OF, ProcurementMethod.TEST_OF -> throw ErrorException(ErrorType.INVALID_PMD)
    }
}
