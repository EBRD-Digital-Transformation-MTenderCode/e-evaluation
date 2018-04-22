package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.ocds.Period;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public interface PeriodService {

    Period saveStartOfPeriod(String cpId, String stage, LocalDateTime startDate);

    Period saveEndOfPeriod(String cpId, String stage, LocalDateTime endDate);
}
