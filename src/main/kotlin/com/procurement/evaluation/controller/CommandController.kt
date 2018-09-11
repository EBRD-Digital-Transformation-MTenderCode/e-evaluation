package com.procurement.evaluation.controller

import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.CommandType
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.service.UpdateAwardService
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
class CommandController(private val updateAwardService: UpdateAwardService) {

    @PostMapping
    fun command(@RequestBody commandMessage: CommandMessage): ResponseEntity<ResponseDto> {
        return ResponseEntity(execute(commandMessage), HttpStatus.OK)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        return when (cm.command) {
            CommandType.GET_UPDATED_AWARDS_FOR_CANS -> updateAwardService.getUpdatedAwardsForCAN(cm)
        }
    }
}



