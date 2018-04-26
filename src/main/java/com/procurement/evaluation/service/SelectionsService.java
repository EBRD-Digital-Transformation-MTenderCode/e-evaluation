package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public interface SelectionsService {

    ResponseDto createAwards(String cpId,
                             String stage,
                             String owner,
                             String country,
                             String pmd,
                             String awardCriteria,
                             LocalDateTime startDate,
                             SelectionsRequestDto dataDto);
}
