package com.procurement.evaluation.controller;

import com.procurement.evaluation.model.dto.award.AwardRequestDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseDto;
import com.procurement.evaluation.service.AwardService;
import com.procurement.evaluation.service.PeriodService;
import com.procurement.evaluation.service.SelectionsService;
import java.time.LocalDateTime;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/evaluation")
public class SelectionsController {
    private final SelectionsService selectionsService;
    private final AwardService awardService;
    private final PeriodService periodService;

    public SelectionsController(final SelectionsService selectionsService,
                                final AwardService awardService, final PeriodService periodService) {
        this.selectionsService = selectionsService;
        this.awardService = awardService;
        this.periodService = periodService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> selections(@RequestParam(value = "identifier") final String cpid,
                                                  @RequestParam(value = "stage") final String stage,
                                                  @RequestParam(value = "country") final String country,
                                                  @RequestParam(value = "pmd") final String pmd,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam(value = "date") final LocalDateTime startDate,
                                                  @RequestParam(value = "awardCriteria") final String awardCriteria,
                                                  @Valid @RequestBody final SelectionsRequestDto data) {
        data.setCpId(cpid);
        data.setStage(stage);
        data.setCountry(country);
        data.setProcurementMethodDetails(pmd);
        return new ResponseEntity<>(selectionsService.getAwards(data), HttpStatus.OK);
    }

    @PutMapping(value = "/{cpid}")
    public ResponseEntity<ResponseDto> changeAward(@PathVariable(value = "cpid") final String cpid,
                                                   @RequestParam(value = "token") final String token,
                                                   @RequestParam(value = "owner") final String owner,
                                                   @Valid @RequestBody final AwardRequestDto data) {
        data.setCpId(cpid);
        data.setToken(token);
        data.setOwner(owner);
        return new ResponseEntity<>(awardService.updateAwardDto(data), HttpStatus.OK);
    }

    @PostMapping(value = "/end_period/{cpid}")
    public ResponseEntity<ResponseDto> finalAwards(@PathVariable(value = "cpid") final String cpid,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam(value = "endDate") final LocalDateTime endDate) {

        return new ResponseEntity<>(periodService.endPeriod(cpid, endDate), HttpStatus.OK);
    }
}
