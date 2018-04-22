package com.procurement.evaluation.controller;

import com.procurement.evaluation.model.dto.UpdateAwardRequestDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
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
public class EvaluationController {
    private final SelectionsService selectionsService;
    private final AwardService awardService;
    private final PeriodService periodService;

    public EvaluationController(final SelectionsService selectionsService,
                                final AwardService awardService, final PeriodService periodService) {
        this.selectionsService = selectionsService;
        this.awardService = awardService;
        this.periodService = periodService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createAwards(@RequestParam(value = "identifier") final String cpId,
                                                    @RequestParam(value = "stage") final String stage,
                                                    @RequestParam(value = "owner") final String owner,
                                                    @RequestParam(value = "country") final String country,
                                                    @RequestParam(value = "pmd") final String pmd,
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    @RequestParam(value = "date") final LocalDateTime startDate,
                                                    @Valid @RequestBody final SelectionsRequestDto data) {
        return new ResponseEntity<>(
                selectionsService.createAwards(cpId, stage, owner, country, pmd, startDate, data),
                HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ResponseDto> updateAward(@RequestParam("identifier") final String cpId,
                                                   @RequestParam("stage") final String stage,
                                                   @RequestParam("token") final String token,
                                                   @RequestParam("owner") final String owner,
                                                   @Valid @RequestBody final UpdateAwardRequestDto data) {
        return new ResponseEntity<>(
                awardService.updateAward(cpId, stage, token, owner, data),
                HttpStatus.OK);
    }

    @PostMapping(value = "/end_period/{cpid}")
    public ResponseEntity<ResponseDto> finalAwards(@PathVariable(value = "cpid") final String cpid,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam(value = "endDate") final LocalDateTime endDate) {

        return new ResponseEntity<>(
                periodService.endPeriod(cpid, endDate),
                HttpStatus.OK);
    }
}
