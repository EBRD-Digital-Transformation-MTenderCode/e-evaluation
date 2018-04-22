package com.procurement.evaluation.service;

import com.procurement.evaluation.exception.ErrorException;
import com.procurement.evaluation.exception.ErrorType;
import com.procurement.evaluation.model.dto.ocds.Period;
import com.procurement.evaluation.model.entity.PeriodEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.repository.PeriodRepository;
import com.procurement.evaluation.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PeriodServiceImpl implements PeriodService {

    private final PeriodRepository periodRepository;
    private final AwardRepository awardRepository;
    private final JsonUtil jsonUtil;

    public PeriodServiceImpl(final PeriodRepository periodRepository,
                             final AwardRepository awardRepository,
                             final JsonUtil jsonUtil) {
        this.periodRepository = periodRepository;
        this.awardRepository = awardRepository;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public Period saveStartOfPeriod(final String cpId, final String stage, final LocalDateTime startDate) {
        PeriodEntity periodEntity = new PeriodEntity();
        periodEntity.setCpId(cpId);
        periodEntity.setStage(stage);
        periodEntity.setStartDate(startDate);
        periodEntity = periodRepository.save(periodEntity);
        final Period period = new Period(periodEntity.getStartDate(), null);
        return period;
    }

    @Override
    public Period saveEndOfPeriod(final String cpId, final String stage, final LocalDateTime endDate) {
        final PeriodEntity periodEntity = Optional.ofNullable(periodRepository.getByCpIdAndStage(cpId, stage))
                .orElseThrow(() -> new ErrorException(ErrorType.PERIOD_NOT_FOUND));
        periodEntity.setEndDate(endDate);
        periodRepository.save(periodEntity);
        final Period period = new Period(periodEntity.getStartDate(), periodEntity.getEndDate());
        return period;
    }
}
