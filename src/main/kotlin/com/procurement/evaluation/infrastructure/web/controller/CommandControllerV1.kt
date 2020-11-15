package com.procurement.evaluation.infrastructure.web.controller

import com.procurement.evaluation.exception.EnumException
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.api.v1.ApiResponseV1
import com.procurement.evaluation.infrastructure.api.v1.CommandMessage
import com.procurement.evaluation.infrastructure.api.v1.commandId
import com.procurement.evaluation.infrastructure.configuration.properties.GlobalProperties
import com.procurement.evaluation.infrastructure.service.CommandServiceV1
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/command")
class CommandControllerV1(private val commandService: CommandServiceV1) {
    companion object {
        private val log = LoggerFactory.getLogger(CommandControllerV1::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponseV1> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")

        val cm: CommandMessage = try {
            toObject(CommandMessage::class.java, requestBody)
        } catch (exception: Exception) {
            val response = errorResponse(exception, CommandId.NaN, ApiVersion.NaN)
            return ResponseEntity(response, HttpStatus.OK)
        }

        val response = try {
            commandService.execute(cm)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (operation-id: '${cm.context.operationId}'): '${toJson(response)}'.")
                }
        } catch (exception: Exception) {
            errorResponse(exception, cm.commandId, cm.version)
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    fun errorResponse(exception: Exception, id: CommandId, version: ApiVersion) = when (exception) {
        is ErrorException ->
            getApiResponse(version = version, id = id, code = exception.code, message = exception.message!!)

        is EnumException ->
            getApiResponse(version = version, id = id, code = exception.code, message = exception.message!!)

        else -> getApiResponse(version = version, id = id, code = "00.00", message = exception.message!!)
    }

    private fun getApiResponse(id: CommandId, version: ApiVersion, code: String, message: String) = ApiResponseV1.Failure(
        version = version,
        id = id,
        errors = listOf(
            ApiResponseV1.Failure.Error(
                code = "400.${GlobalProperties.service.id}." + code,
                description = message
            )
        )
    )
}
