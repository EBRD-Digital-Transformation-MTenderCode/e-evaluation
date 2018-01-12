package com.procurement.evaluation.service;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.model.entity.AwardPeriodEntity;
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

   /* @Override
    public PeriodResponseDto getAwards(final PeriodDto dataDto) {

        final String ocId = dataDto.getOcid();

        saveEndOfPeriod(ocId, dataDto.getTender()
                                     .getAwardPeriod()
                                     .getEndDate());

        final AwardPeriodEntity awardPeriodEntity = periodRepository.getByOcId(ocId);

        final AwardPeriodDto awardPeriodDto = new AwardPeriodDto(awardPeriodEntity.getStartDate(), awardPeriodEntity
            .getEndDate());

        final List<AwardEntity> awardPeriodEntities = awardRepository.selectAwardsEntityByOcid(ocId);

        final List<AwardBidsResponseDto> awardBidsResponseDtos = getAwardsDtoFromEntity(awardPeriodEntities);

        final PeriodResponseDto responseDto = new PeriodResponseDto(ocId, awardPeriodDto, awardBidsResponseDtos, null);

        return responseDto;
    }*/

    @Override
    public AwardPeriodDto saveStartOfPeriod(final String ocId, final LocalDateTime startDate) {
        AwardPeriodEntity periodEntity = new AwardPeriodEntity();
        periodEntity.setOcId(ocId);
        periodEntity.setStartDate(startDate);
        periodEntity = periodRepository.save(periodEntity);
        final AwardPeriodDto awardPeriodDto = new AwardPeriodDto(periodEntity.getStartDate(),
                                                                 periodEntity.getEndDate());
        return awardPeriodDto;
    }
    @Override
    public AwardPeriodDto saveEndOfPeriod(final String ocId, final LocalDateTime endDate) {
        final AwardPeriodEntity periodEntity = periodRepository.getByOcId(ocId);
        periodEntity.setEndDate(endDate);
        periodRepository.save(periodEntity);
        final AwardPeriodDto awardPeriodDto = new AwardPeriodDto(periodEntity.getStartDate(),
                                                                 periodEntity.getEndDate());
        return awardPeriodDto;
    }

    /*



    @Override
    public boolean isAllAwardsAreValid(final List<AwardBidsResponseDto> awards) {

        for (int i = 0; i < awards.size(); i++) {
            if (isAwardStatusDetailsOk(awards.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getErrorMessageFromAwards(final List<AwardBidsResponseDto> awards) {
        String message = "";
        for (int i = 0; i < awards.size(); i++) {
            if (isAwardStatusDetailsOk(awards.get(i))) {
                message += awards.get(i)
                                 .getId() + " without status details; ";
            }
        }
        return message;
    }

    private boolean isAwardStatusDetailsOk(AwardBidsResponseDto awardBidsResponseDto){
        if (awardBidsResponseDto.getStatus()!=AwardBidsResponseDto.Status.UNSUCCESSFUL &&
            awardBidsResponseDto.getStatusDetails()==null){
            return false;
        }
        return true;

    }*/
}
