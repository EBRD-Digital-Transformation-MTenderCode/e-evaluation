package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.requirement.response.AddRequirementResponseParams
import com.procurement.evaluation.infrastructure.dto.award.create.requirement.response.AddRequirementResponseRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result

fun AddRequirementResponseRequest.convert(): Result<AddRequirementResponseParams, DataErrors> =
    AddRequirementResponseParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        award = award.convert()
            .onFailure { return it }
    )

private fun AddRequirementResponseRequest.Award.convert()
    : Result<AddRequirementResponseParams.Award, DataErrors> =
    AddRequirementResponseParams.Award.tryCreate(
        id = id,
        requirementResponse = requirementResponse.convert()
            .onFailure { return it }
    )

private fun AddRequirementResponseRequest.Award.RequirementResponse.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.tryCreate(
        id = id,
        value = value,
        relatedTenderer = relatedTenderer.convert()
            .onFailure { return it },
        requirement = requirement.convert()
            .onFailure { return it },
        responder = responder.convert()
            .onFailure { return it }
    )

private fun AddRequirementResponseRequest.Award.RequirementResponse.RelatedTenderer.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse.RelatedTenderer, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.RelatedTenderer.tryCreate(id)

private fun AddRequirementResponseRequest.Award.RequirementResponse.Requirement.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse.Requirement, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.Requirement.tryCreate(id)

private fun AddRequirementResponseRequest.Award.RequirementResponse.Responder.convert()
    : Result<AddRequirementResponseParams.Award.RequirementResponse.Responder, DataErrors> =
    AddRequirementResponseParams.Award.RequirementResponse.Responder.tryCreate(
        id = id,
        name = name
    )