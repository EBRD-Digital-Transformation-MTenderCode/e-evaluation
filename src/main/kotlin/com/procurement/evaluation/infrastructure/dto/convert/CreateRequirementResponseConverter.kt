package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.requirement.response.CreateRequirementResponseParams
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.award.create.requirement.response.CreateRequirementResponseRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

fun CreateRequirementResponseRequest.convert(): Result<CreateRequirementResponseParams, DataErrors> =
    CreateRequirementResponseParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        award = award.convert()
            .forwardResult { result -> return result }
    )

fun CreateRequirementResponseRequest.Award.convert()
    : Result<CreateRequirementResponseParams.Award, DataErrors> =
    CreateRequirementResponseParams.Award.tryCreate(
        id = id,
        owner = owner,
        token = token,
        requirementResponse = requirementResponse.convert()
            .forwardResult { result -> return result }
    )

fun CreateRequirementResponseRequest.Award.RequirementResponse.convert()
    : Result<CreateRequirementResponseParams.Award.RequirementResponse, DataErrors> =
    CreateRequirementResponseParams.Award.RequirementResponse.tryCreate(
        id = id,
        value = value,
        relatedTenderer = relatedTenderer.convert()
            .forwardResult { result -> return result },
        requirement = requirement.convert()
            .forwardResult { result -> return result },
        responderer = responderer.convert()
            .forwardResult { result -> return result }
    )

fun CreateRequirementResponseRequest.Award.RequirementResponse.RelatedTenderer.convert()
    : Result<CreateRequirementResponseParams.Award.RequirementResponse.RelatedTenderer, DataErrors> =
    CreateRequirementResponseParams.Award.RequirementResponse.RelatedTenderer.tryCreate(
        id
    )

fun CreateRequirementResponseRequest.Award.RequirementResponse.Requirement.convert()
    : Result<CreateRequirementResponseParams.Award.RequirementResponse.Requirement, DataErrors> =
    CreateRequirementResponseParams.Award.RequirementResponse.Requirement.tryCreate(
        id
    )

fun CreateRequirementResponseRequest.Award.RequirementResponse.Responderer.convert()
    : Result<CreateRequirementResponseParams.Award.RequirementResponse.Responderer, DataErrors> =
    CreateRequirementResponseParams.Award.RequirementResponse.Responderer.tryCreate(
        id, name
    )
