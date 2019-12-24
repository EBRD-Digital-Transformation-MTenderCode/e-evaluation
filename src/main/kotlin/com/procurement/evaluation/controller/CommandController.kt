package com.procurement.evaluation.controller

import com.procurement.evaluation.exception.EnumException
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.infrastructure.dto.ApiErrorResponse
import com.procurement.evaluation.infrastructure.dto.ApiResponse
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.getEnumExceptionResponseDto
import com.procurement.evaluation.model.dto.bpe.getErrorExceptionResponseDto
import com.procurement.evaluation.model.dto.bpe.getExceptionResponseDto
import com.procurement.evaluation.service.CommandService
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/command")
class CommandController(private val commandService: CommandService) {
    companion object {
        private val log = LoggerFactory.getLogger(CommandController::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '$requestBody'.")
        val cm: CommandMessage = toObject(CommandMessage::class.java, requestBody)

        val response = try {
            commandService.execute(cm).also { response ->
                if (log.isDebugEnabled)
                    log.debug("RESPONSE (operation-id: '${cm.context.operationId}'): '${toJson(response)}'.")
            }
        } catch (exception: Exception) {
            when (exception) {
                is ErrorException -> getErrorExceptionResponseDto(exception, cm.id, cm.version)
                is EnumException -> getEnumExceptionResponseDto(exception, cm.id, cm.version)
                else -> getExceptionResponseDto(exception)
            }
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ApiErrorResponse {
        log.error("Internal error", ex)
        return when (ex) {
            is ErrorException -> getErrorExceptionResponseDto(ex)
            is EnumException -> getEnumExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }
}



