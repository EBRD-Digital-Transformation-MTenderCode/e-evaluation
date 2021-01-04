package com.procurement.evaluation.infrastructure.service

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.fail.error.BadRequest
import com.procurement.evaluation.infrastructure.handler.v2.AddRequirementResponseHandler
import com.procurement.evaluation.infrastructure.handler.v2.CheckAccessToAwardHandler
import com.procurement.evaluation.infrastructure.handler.v2.CheckAwardsStateHandler
import com.procurement.evaluation.infrastructure.handler.v2.CheckRelatedTendererHandler
import com.procurement.evaluation.infrastructure.handler.v2.CloseAwardPeriodHandler
import com.procurement.evaluation.infrastructure.handler.v2.CreateAwardHandler
import com.procurement.evaluation.infrastructure.handler.v2.CreateUnsuccessfulAwardsHandler
import com.procurement.evaluation.infrastructure.handler.v2.FindAwardsForProtocolHandler
import com.procurement.evaluation.infrastructure.handler.v2.GetAwardByIdsHandler
import com.procurement.evaluation.infrastructure.handler.v2.GetAwardStateByIdsHandler
import com.procurement.evaluation.infrastructure.handler.v2.StartAwardPeriodHandler
import com.procurement.evaluation.infrastructure.handler.v2.UpdateAwardHandler
import com.procurement.evaluation.infrastructure.handler.v2.ValidateAwardDataHandler
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import org.springframework.stereotype.Service

@Service
class CommandServiceV2(
    private val logger: Logger,
    private val getAwardStateByIdsHandler: GetAwardStateByIdsHandler,
    private val getAwardByIdsHandler: GetAwardByIdsHandler,
    private val findAwardsForProtocolHandler: FindAwardsForProtocolHandler,
    private val checkAccessToAwardHandler: CheckAccessToAwardHandler,
    private val checkRelatedTendererHandler: CheckRelatedTendererHandler,
    private val addRequirementResponseHandler: AddRequirementResponseHandler,
    private val createUnsuccessfulAwardHandler: CreateUnsuccessfulAwardsHandler,
    private val closeAwardPeriodHandler: CloseAwardPeriodHandler,
    private val checkAwardsStateHandler: CheckAwardsStateHandler,
    private val startAwardPeriodHandler: StartAwardPeriodHandler,
    private val validateAwardDataHandler: ValidateAwardDataHandler,
    private val createAwardHandler: CreateAwardHandler,
    private val updateAwardHandler: UpdateAwardHandler
) {

    fun execute(descriptor: CommandDescriptor): ApiResponseV2 = when (descriptor.action) {
        CommandTypeV2.GET_AWARD_STATES_BY_IDS -> getAwardStateByIdsHandler.handle(descriptor)
        CommandTypeV2.GET_AWARD_BY_IDS -> getAwardByIdsHandler.handle(descriptor)
        CommandTypeV2.FIND_AWARDS_FOR_PROTOCOL -> findAwardsForProtocolHandler.handle(descriptor)
        CommandTypeV2.CHECK_ACCESS_TO_AWARD -> checkAccessToAwardHandler.handle(descriptor)
        CommandTypeV2.CHECK_RELATED_TENDERER -> checkRelatedTendererHandler.handle(descriptor)
        CommandTypeV2.ADD_REQUIREMENT_RESPONSE -> addRequirementResponseHandler.handle(descriptor)
        CommandTypeV2.CREATE_UNSUCCESSFUL_AWARDS -> createUnsuccessfulAwardHandler.handle(descriptor)
        CommandTypeV2.CLOSE_AWARD_PERIOD -> closeAwardPeriodHandler.handle(descriptor)
        CommandTypeV2.CHECK_AWARDS_STATE -> checkAwardsStateHandler.handle(descriptor)
        CommandTypeV2.START_AWARD_PERIOD -> startAwardPeriodHandler.handle(descriptor)
        CommandTypeV2.VALIDATE_AWARD_DATA -> validateAwardDataHandler.handle(descriptor)
        CommandTypeV2.CREATE_AWARD -> createAwardHandler.handle(descriptor)
        CommandTypeV2.UPDATE_AWARD -> updateAwardHandler.handle(descriptor)
        else -> {
            val errorDescription = "Unknown action '${descriptor.action.key}'."
            generateResponseOnFailure(
                fail = BadRequest(description = errorDescription, RuntimeException(errorDescription)),
                id = descriptor.id,
                version = descriptor.version,
                logger = logger
            )
        }
    }
}
