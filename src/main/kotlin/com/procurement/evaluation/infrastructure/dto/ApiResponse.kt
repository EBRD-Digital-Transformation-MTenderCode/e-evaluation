package com.procurement.evaluation.infrastructure.dto

import com.procurement.evaluation.model.dto.bpe.ApiVersion
import com.procurement.evaluation.model.dto.bpe.ResponseErrorDto

sealed class ApiResponse {
    abstract val id: String
    abstract val version: ApiVersion
}

class ApiErrorResponse(
    override val id: String,
    override val version: ApiVersion,
    val errors: List<ResponseErrorDto>
) : ApiResponse()

class ApiSuccessResponse(
    override val id: String,
    override val version: ApiVersion,
    val data: Any?
) : ApiResponse()
