package com.procurement.evaluation.controller

import com.procurement.evaluation.model.dto.UpdateAwardRequestDto
import com.procurement.evaluation.model.dto.awardByBid.AwardByBidRequestDto
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto
import com.procurement.evaluation.service.AwardService
import com.procurement.evaluation.service.ProcessService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/evaluation")
class EvaluationController(private val processService: ProcessService,
                           private val awardService: AwardService) {

    @PostMapping
    fun createAwards(@RequestParam(value = "cpid") cpId: String,
                     @RequestParam(value = "stage") stage: String,
                     @RequestParam(value = "owner") owner: String,
                     @RequestParam(value = "country") country: String,
                     @RequestParam(value = "pmd") pmd: String,
                     @RequestParam(value = "awardCriteria") awardCriteria: String,
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                     @RequestParam(value = "date") dateTime: LocalDateTime,
                     @Valid @RequestBody data: SelectionsRequestDto): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                awardService.createAwards(
                        cpId = cpId,
                        stage = stage,
                        owner = owner,
                        country = country,
                        pmd = pmd,
                        awardCriteria = awardCriteria,
                        startDate = dateTime,
                        dto = data),
                HttpStatus.CREATED)
    }

    @GetMapping
    fun getAwards(@RequestParam("cpid") cpId: String,
                  @RequestParam("stage") stage: String,
                  @RequestParam("country") country: String,
                  @RequestParam("pmd") pmd: String): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                awardService.getAwards(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd),
                HttpStatus.OK)
    }

    @PutMapping
    fun updateAward(@RequestParam("cpid") cpId: String,
                    @RequestParam("stage") stage: String,
                    @RequestParam("token") token: String,
                    @RequestParam("awardId") awardId: String,
                    @RequestParam("owner") owner: String,
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    @RequestParam(value = "date") dateTime: LocalDateTime,
                    @Valid @RequestBody data: UpdateAwardRequestDto): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                processService.updateAndGetNextAward(
                        cpId = cpId,
                        stage = stage,
                        token = token,
                        awardId = awardId,
                        owner = owner,
                        dateTime = dateTime,
                        dto = data),
                HttpStatus.OK)
    }

    @PostMapping("/endAwardPeriod")
    fun endAwardPeriod(@RequestParam("cpid") cpId: String,
                       @RequestParam("stage") stage: String,
                       @RequestParam("country") country: String,
                       @RequestParam("pmd") pmd: String,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                       @RequestParam("endPeriod") endPeriod: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                processService.endAwardPeriod(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd,
                        endPeriod = endPeriod),
                HttpStatus.OK)
    }


    @PostMapping("/awardByBid")
    fun awardByBid(@RequestParam("token") token: String,
                   @RequestParam("owner") owner: String,
                   @RequestParam("cpid") cpId: String,
                   @RequestParam("stage") stage: String,
                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                   @RequestParam(value = "date") dateTime: LocalDateTime,
                   @RequestParam("awardId")awardId: String,
                   @Valid @RequestBody data: AwardByBidRequestDto):ResponseEntity<ResponseDto>{
        return ResponseEntity(
            processService.awardByBid(
                cpId = cpId,
                stage = stage,
                token = token,
                awardId = awardId,
                owner = owner,
                dateTime = dateTime,
                dto = data
            ),HttpStatus.OK
        )

    }
}
