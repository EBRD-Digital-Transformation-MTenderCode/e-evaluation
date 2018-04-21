package com.procurement.evaluation.service;

import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.Status;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.endbid.EndBidAwardRSDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.model.entity.PeriodEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.repository.PeriodRepository;
import com.procurement.evaluation.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public AwardPeriodDto saveStartOfPeriod(final String ocId, final LocalDateTime startDate) {
        PeriodEntity periodEntity = new PeriodEntity();
        periodEntity.setOcId(ocId);
        periodEntity.setStartDate(startDate);
        periodEntity = periodRepository.save(periodEntity);
        final AwardPeriodDto awardPeriodDto = new AwardPeriodDto(periodEntity.getStartDate(),
                                                                 periodEntity.getEndDate());
        return awardPeriodDto;
    }

    @Override
    public AwardPeriodDto saveEndOfPeriod(final String ocId, final LocalDateTime endDate) {
        final PeriodEntity periodEntity = periodRepository.getByOcId(ocId);
        periodEntity.setEndDate(endDate);
        periodRepository.save(periodEntity);
        final AwardPeriodDto awardPeriodDto = new AwardPeriodDto(periodEntity.getStartDate(),
                                                                 periodEntity.getEndDate());
        return awardPeriodDto;
    }

    @Override
    public ResponseDto endPeriod(final String cpId, final LocalDateTime endPeriod) {
        final PeriodEntity periodEntity = periodRepository.getByOcId(cpId);
        periodEntity.setEndDate(endPeriod);
        periodRepository.save(periodEntity);

        final List<AwardEntity> awardEntities = awardRepository.selectAwardsEntityByOcid(cpId);

        for (int i = 0; i < awardEntities.size(); i++) {
            if (awardEntities.get(i)
                             .getStatusDetails() != null) {
                awardEntities.get(i)
                             .setStatus(awardEntities.get(i)
                                                     .getStatusDetails());
                awardRepository.save(awardEntities.get(i));
            }

            final List<EndBidAwardRSDto> awardBidRSDtos = getAwardsDtoFromEntity(awardEntities);

            return new ResponseDto(true, null, awardBidRSDtos);
        }
        return null;
    }

    private List<EndBidAwardRSDto> getAwardsDtoFromEntity(final List<AwardEntity> awardEntities) {
        final List<EndBidAwardRSDto> awardBidRSDtos = new ArrayList<>();
        for (int i = 0; i < awardEntities.size(); i++) {
            final EndBidAwardRSDto bidAwardRSDto = jsonUtil.toObject(EndBidAwardRSDto.class, awardEntities.get(i)
                                                                                                          .getJsonData());
            bidAwardRSDto.setStatus(Status.fromValue(awardEntities.get(i)
                                                                  .getStatus()));
            bidAwardRSDto.setStatusDetails(null);

            awardBidRSDtos.add(bidAwardRSDto);
        }

        return awardBidRSDtos;
    }
}
