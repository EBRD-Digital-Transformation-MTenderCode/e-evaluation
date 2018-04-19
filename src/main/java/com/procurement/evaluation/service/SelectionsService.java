package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface SelectionsService {

    ResponseDto getAwards(SelectionsRequestDto dataDto);
}
