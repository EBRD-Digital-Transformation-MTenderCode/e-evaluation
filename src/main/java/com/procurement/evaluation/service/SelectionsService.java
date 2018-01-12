package com.procurement.evaluation.service;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface SelectionsService {

    SelectionsResponseDto getAwards(SelectionsRequestDto dataDto);
}
