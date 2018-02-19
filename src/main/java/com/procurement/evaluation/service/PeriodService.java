package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public interface PeriodService {

    AwardPeriodDto saveStartOfPeriod(String cpId, LocalDateTime startDate);

    AwardPeriodDto saveEndOfPeriod(String cpId, LocalDateTime endDate);

    ResponseDto endPeriod(String cpId, LocalDateTime endPeriod);
}
