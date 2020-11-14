package com.procurement.evaluation.infrastructure.web.controller

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.api.v2.ApiResponse2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.api.v2.tryGetId
import com.procurement.evaluation.infrastructure.api.v2.tryGetNode
import com.procurement.evaluation.infrastructure.api.v2.tryGetVersion
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.service.CommandServiceV2
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.utils.toJson
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command2")
class CommandControllerV2(private val commandService: CommandServiceV2, private val logger: Logger) {

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse2> {
        if (logger.isDebugEnabled)
            logger.debug("RECEIVED COMMAND: '$requestBody'.")

        val node = requestBody.tryGetNode()
            .onFailure { return generateResponse(fail = it.reason, id = CommandId.NaN, version = ApiVersion.NaN) }

        val version = when (val versionResult = node.tryGetVersion()) {
            is Result.Success -> versionResult.get
            is Result.Failure -> when (val idResult = node.tryGetId()) {
                is Result.Success ->
                    return generateResponse(fail = versionResult.reason, id = idResult.get, version = ApiVersion.NaN)
                is Result.Failure ->
                    return generateResponse(fail = versionResult.reason, id = CommandId.NaN, version = ApiVersion.NaN)
            }

        }

        val id = node.tryGetId()
            .onFailure { return generateResponse(fail = it.reason, version = version, id = CommandId.NaN) }

        val response =
            commandService.execute(node)
                .also { response ->
                    if (logger.isDebugEnabled)
                        logger.debug("RESPONSE (id: '${id}'): '${toJson(response)}'.")
                }

        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun generateResponse(
        fail: Fail,
        version: ApiVersion,
        id: CommandId
    ): ResponseEntity<ApiResponse2> {
        val response = generateResponseOnFailure(fail = fail, id = id, version = version, logger = logger)
        return ResponseEntity(response, HttpStatus.OK)
    }
}