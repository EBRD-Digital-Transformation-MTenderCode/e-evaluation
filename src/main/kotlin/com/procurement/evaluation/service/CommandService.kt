package com.procurement.evaluation.service

import com.procurement.evaluation.dao.HistoryDao
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.CommandType
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service

@Service
class CommandService(private val historyDao: HistoryDao,
                     private val createAwardService: CreateAwardService,
                     private val updateAwardService: UpdateAwardService,
                     private val statusService: StatusService) {


    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CREATE_AWARDS -> createAwardService.createAwards(cm)
            CommandType.AWARD_BY_BID -> updateAwardService.awardByBid(cm)
            CommandType.AWARDS_FOR_CANS -> updateAwardService.awardsForCans(cm)
            CommandType.SET_FINAL_STATUSES -> statusService.setFinalStatuses(cm)
            CommandType.PREPARE_CANCELLATION -> statusService.prepareCancellation(cm)
            CommandType.AWARDS_CANCELLATION -> statusService.awardsCancellation(cm)
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }
}