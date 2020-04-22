package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.requirement.response.AddRequirementResponseParams
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.award.create.requirement.response.AddRequirementResponseRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

fun AddRequirementResponseRequest.convert(): Result<AddRequirementResponseParams, DataErrors> =
    AddRequirementResponseParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        award = award.convert()
            .orForwardFail { result -> return result }
    )

private fun AddRequirementResponseRequest.Award.convert()
    : Result<AddRequirementResponseParams.Award, DataErrors> =
    AddRequirementResponseParams.Award.tryCreate(
        id = id,
        requirementResponse = requirementResponse.convert()
            .orForwardFail { result -> return result }
    )

private fun AddRequirementResponseRequest.Award.RequirementResponse.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.tryCreate(
        id = id,
        value = value,
        relatedTenderer = relatedTenderer.convert()
            .orForwardFail { result -> return result },
        requirement = requirement.convert()
            .orForwardFail { result -> return result },
        responderer = responderer.convert()
            .orForwardFail { result -> return result }
    )

private fun AddRequirementResponseRequest.Award.RequirementResponse.RelatedTenderer.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse.RelatedTenderer, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.RelatedTenderer.tryCreate(id)

private fun AddRequirementResponseRequest.Award.RequirementResponse.Requirement.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse.Requirement, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.Requirement.tryCreate(id)

private fun AddRequirementResponseRequest.Award.RequirementResponse.Responderer.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse.Responderer, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.Responderer.tryCreate(id, name)