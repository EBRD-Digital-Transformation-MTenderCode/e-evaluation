package com.procurement.evaluation.controller;

import com.procurement.evaluation.exception.ValidationException;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping(value = "/{cpid}")
    public ResponseEntity<ResponseDto> selections(
        @Valid @RequestBody final SelectionsRequestDto data,
        @PathVariable(value = "cpid") final String cpid,
        @RequestParam(value = "stage") final String stage,
        @RequestParam(value = "country") final String country,
        @RequestParam(value = "pmd") final String pmd,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @RequestParam(value = "startDate") final LocalDateTime startDate,
        @RequestParam(value = "awardCriteria") final String awardCriteria,
        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        data.setCpId(cpid);
        data.setStage(stage);
        data.setCountry(country);
        data.setProcurementMethodDetails(pmd);

        final SelectionsResponseDto responseData = selectionsService.getAwards(data);
        final ResponseDto responseDto = new ResponseDto(true, null, responseData);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping(value = "/{cpid}")
    public ResponseEntity<ResponseDto> changeAward(@Valid @RequestBody final AwardRequestDto data,
                                                   @PathVariable(value = "cpid") final String cpid,
                                                   @RequestParam(value = "token") final String token,
                                                   @RequestParam(value = "owner") final String owner,
                                                   final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        data.setCpId(cpid);
        data.setToken(token);
        data.setOwner(owner);

        final ResponseDto responseDto = awardService.updateAwardDto(data);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping(value = "/end_period/{cpid}")
    public ResponseEntity<ResponseDto> finalAwards(@PathVariable(value = "cpid") final String cpid,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam(value = "endDate") final LocalDateTime endDate) {

        final ResponseDto responseDto = periodService.endPeriod(cpid, endDate);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
