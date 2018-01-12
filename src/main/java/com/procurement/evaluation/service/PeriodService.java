package com.procurement.evaluation.service;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PeriodService {

  //  PeriodResponseDto getAwards(PeriodDto dataDto);

    AwardPeriodDto saveStartOfPeriod(String ocId, LocalDateTime startDate);

    AwardPeriodDto saveEndOfPeriod(String ocId, LocalDateTime endDate);



   // boolean isAllAwardsAreValid(List<AwardBidsResponseDto> awards);

   // String getErrorMessageFromAwards(List<AwardBidsResponseDto> awards);
}
