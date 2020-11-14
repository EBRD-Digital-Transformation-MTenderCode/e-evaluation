package com.procurement.evaluation.infrastructure.web.controller

import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.api.v1.ApiResponse
import com.procurement.evaluation.infrastructure.api.v1.CommandMessage
import com.procurement.evaluation.infrastructure.api.v1.commandId
import com.procurement.evaluation.infrastructure.api.v1.errorResponseDto
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
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")

        val cm: CommandMessage = try {
            toObject(CommandMessage::class.java, requestBody)
        } catch (exception: Exception) {
            val response = errorResponseDto(exception, CommandId.NaN, ApiVersion.NaN)
            return ResponseEntity(response, HttpStatus.OK)
        }

        val response = try {
            commandService.execute(cm)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (operation-id: '${cm.context.operationId}'): '${toJson(response)}'.")
                }
        } catch (exception: Exception) {
            errorResponseDto(exception, cm.commandId, cm.version)
        }
        return ResponseEntity(response, HttpStatus.OK)
    }
}
