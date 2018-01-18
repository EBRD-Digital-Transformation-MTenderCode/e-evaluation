package com.procurement.evaluation.controller;

import com.procurement.evaluation.exception.ValidationException;
import com.procurement.evaluation.model.dto.award.AwardRequestDto;
import com.procurement.evaluation.model.dto.award.AwardResponseDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.endbid.EndBidDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evaluation")
public class SelectionsController {
    private final SelectionsService selectionsService;
    private final AwardService awardService;
    private final PeriodService periodService;

    public SelectionsController(SelectionsService selectionsService,
                                AwardService awardService, PeriodService periodService) {
        this.selectionsService = selectionsService;
        this.awardService = awardService;
        this.periodService = periodService;
    }

    @RequestMapping(value = "/{cpid}", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> selections(
        @Valid @RequestBody SelectionsRequestDto data,
        @PathVariable(value = "cpid") String cpid,
        @RequestParam(value = "stage") String stage,
        @RequestParam(value = "country") String country,
        @RequestParam(value = "pmd") String pmd,
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

    @RequestMapping(value = "/{cpid}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseDto> changeAward(@Valid @RequestBody AwardRequestDto data,
                                                   final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        final AwardResponseDto responseData = awardService.updateAwardDto(data);
        final ResponseDto responseDto = new ResponseDto(true, null, responseData);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/patch_end_bid", method = RequestMethod.POST)
    public ResponseEntity<ResponseDto> finalAwards(@RequestBody EndBidDto data) {
        return null;
    }


}
