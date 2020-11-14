package com.procurement.evaluation.infrastructure.api.v2

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.domain.util.extension.nowDefaultUTC
import com.procurement.evaluation.domain.util.extension.toListOrEmpty
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.configuration.properties.GlobalProperties
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import java.util.*

object ApiResponseV2Generator {

    fun generateResponseOnFailure(fail: Fail, version: ApiVersion, id: CommandId, logger: Logger): ApiResponse2 {
        fail.logging(logger)
        return when (fail) {
            is Fail.Error -> when (fail) {
                is DataErrors.Validation ->
                    generateDataErrorResponse(id = id, version = version, dataError = fail)
                is ValidationError ->
                    generateValidationErrorResponse(id = id, version = version, validationError = fail)
                else -> generateErrorResponse(id = id, version = version, error = fail)
            }

            is Fail.Incident -> generateIncidentResponse(id = id, version = version, incident = fail)
        }
    }

    private fun generateDataErrorResponse(dataError: DataErrors.Validation, version: ApiVersion, id: CommandId) =
        ApiErrorResponse2(
            version = version,
            id = id,
            result = listOf(
                ApiErrorResponse2.Error(
                    code = getFullErrorCode(dataError.code),
                    description = dataError.description,
                    details = ApiErrorResponse2.Error.Detail.tryCreateOrNull(name = dataError.name).toListOrEmpty()
                )
            )
        )

    private fun generateValidationErrorResponse(validationError: ValidationError, version: ApiVersion, id: CommandId) =
        ApiErrorResponse2(
            version = version,
            id = id,
            result = listOf(
                ApiErrorResponse2.Error(
                    code = getFullErrorCode(validationError.code),
                    description = validationError.description,
                    details = ApiErrorResponse2.Error.Detail.tryCreateOrNull(id = validationError.id).toListOrEmpty()

                )
            )
        )

    private fun generateErrorResponse(version: ApiVersion, id: CommandId, error: Fail.Error) =
        ApiErrorResponse2(
            version = version,
            id = id,
            result = listOf(
                ApiErrorResponse2.Error(
                    code = getFullErrorCode(error.code),
                    description = error.description
                )
            )
        )

    private fun generateIncidentResponse(incident: Fail.Incident, version: ApiVersion, id: CommandId) =
        ApiIncidentResponse2(
            version = version,
            id = id,
            result = ApiIncidentResponse2.Incident(
                date = nowDefaultUTC(),
                id = UUID.randomUUID(),
                service = ApiIncidentResponse2.Incident.Service(
                    id = GlobalProperties.service.id,
                    version = GlobalProperties.service.version,
                    name = GlobalProperties.service.name
                ),
                details = listOf(
                    ApiIncidentResponse2.Incident.Details(
                        code = getFullErrorCode(incident.code),
                        description = incident.description,
                        metadata = null
                    )
                )
            )
        )

    fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties.service.id}"
}