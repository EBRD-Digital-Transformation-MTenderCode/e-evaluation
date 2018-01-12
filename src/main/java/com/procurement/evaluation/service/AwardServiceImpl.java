package com.procurement.evaluation.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.Status;
import com.procurement.evaluation.model.dto.award.AwardBidDto;
import com.procurement.evaluation.model.dto.award.AwardRequestDto;
import com.procurement.evaluation.model.dto.award.AwardResponseDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseAwardDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.utils.DateUtil;
import com.procurement.evaluation.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AwardServiceImpl implements AwardService {

    private final AwardRepository awardRepository;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final PeriodService periodService;

    public AwardServiceImpl(final AwardRepository awardRepository,
                            final JsonUtil jsonUtil,
                            final DateUtil dateUtil,
                            final PeriodService periodService) {
        this.awardRepository = awardRepository;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.periodService = periodService;
    }

    @Override
    @Transactional
    public AwardResponseDto updateAwardDto(final AwardRequestDto awardDto) {
        AwardBidDto awardBidDto = awardDto.getAwards();
        Status awardStatusDetails = awardBidDto.getStatusDetails();
        LocalDateTime localDateTime = dateUtil.getNowUTC();
        String ocid = awardDto.getOcid();
        AwardPeriodDto awardPeriodDto;
        AwardBidDto awardBidResponse;

        List<AwardBidDto> awardBidResponseDtos = new ArrayList<AwardBidDto>();

        AwardResponseDto responseDto = new AwardResponseDto(ocid, null, null, null);

        if (isValidStatusDetail(awardStatusDetails)) {
            switch (awardStatusDetails) {
                case ACTIVE:

                    AwardEntity awardEntity = awardRepository.findAwardEntity(ocid,UUID.fromString(awardBidDto.getId()));

                    awardPeriodDto = periodService.saveEndOfPeriod(ocid, localDateTime);

                   AwardBidDto awardBidDtofromEntity = getAwardDtoFromEntity(awardEntity);

                   awardBidDtofromEntity.setStatusDetails(awardBidDto.getStatusDetails());

                    updateAward(awardBidDtofromEntity, ocid);

                    awardBidResponseDtos.add(awardBidDtofromEntity);

                    responseDto.setAwardPeriod(awardPeriodDto);
                    responseDto.setAwards(awardBidResponseDtos);
                    break;

                case UNSUCCESSFUL:

                    List<AwardEntity> awardEntities = awardRepository.selectAwardsEntityByOcid(ocid);
                    List<AwardBidDto> awardBidDtos = getAwardsDtoFromEntity(awardEntities);

                    int indexRequriedAward = isListAwardContainsOtherAwards(awardBidDtos);

                    if (indexRequriedAward >= 0) {

                        List<AwardBidDto> awardBidDtosConsiderationPending =
                            getSurrentConsiderationAwardAndNextPendingAward(awardBidDtos, indexRequriedAward);

                        awardBidDtosConsiderationPending.get(0)
                                                        .setStatusDetails(awardStatusDetails);
                        awardBidDtosConsiderationPending.get(1)
                                                        .setStatusDetails(Status.CONSIDERATION);

                        updateAwards(awardBidDtosConsiderationPending, ocid);

                        awardPeriodDto = periodService.saveStartOfPeriod(ocid, localDateTime);

                        responseDto.setAwardPeriod(awardPeriodDto);
                        responseDto.setAwards(awardBidDtosConsiderationPending);
                    } else {


                        awardEntity = awardRepository.findAwardEntity(ocid,UUID.fromString(awardBidDto.getId()));
                        awardPeriodDto = periodService.saveEndOfPeriod(ocid, localDateTime);
                        awardBidDtofromEntity = getAwardDtoFromEntity(awardEntity);

                        awardBidDtofromEntity.setStatusDetails(awardBidDto.getStatusDetails());

                        updateAward(awardBidDtofromEntity, ocid);

                        awardBidResponseDtos.add(awardBidDtofromEntity);

                        responseDto.setAwardPeriod(awardPeriodDto);
                        responseDto.setAwards(awardBidResponseDtos);


                    }
                    break;
            }
            return responseDto;
        } else {
            //todo тут наполняем ошибку
            return new AwardResponseDto("sssss", null, null, null);
        }
    }

    @Override
    @Transactional
    public void saveAwards(final List<SelectionsResponseAwardDto> awards,
                           final String ocId) {
        for (final SelectionsResponseAwardDto awardDto : awards) {
            getEntity(awardDto, ocId)
                .ifPresent(awardRepository::save);
        }
    }

    @Override
    @Transactional
    public List<AwardEntity> updateAwards(final List<AwardBidDto> awards,
                                          final String ocId) {
        List<AwardEntity> awardEntities=new ArrayList<>();
        for (final AwardBidDto awardDto : awards) {
            final AwardEntity awardEntity = awardRepository.findAwardEntity(ocId, UUID.fromString(awardDto.getId()));
            updateEntityFromAward(awardEntity, awardDto)
                .ifPresent(awardRepository::save);
            awardEntities.add(awardEntity);
        }
        return awardEntities;
    }

    @Override
    public AwardEntity updateAward(final AwardBidDto award, final String ocId) {

        final AwardEntity awardEntity = awardRepository.findAwardEntity(ocId, UUID.fromString(award.getId()));
        updateEntityFromAward(awardEntity, award)
            .ifPresent(awardRepository::save);
        return awardEntity;

    }

    @Override
    public List<AwardBidDto> getAwardsDtoFromEntity(final List<AwardEntity> awardPeriodEntities) {
        final List<AwardBidDto> awardBidsResponseDtos = new ArrayList<>();

        for (int i = 0; i < awardPeriodEntities.size(); i++) {

            final AwardBidDto periodResponseAwardDto = getAwardDtoFromEntity(awardPeriodEntities.get(i));

            awardBidsResponseDtos.add(periodResponseAwardDto);
        }

        return awardBidsResponseDtos;
    }

    private AwardBidDto getAwardDtoFromEntity(final AwardEntity entity){
        return jsonUtil.toObject(AwardBidDto.class,entity.getJsonData());
    }

    private Optional<AwardEntity> updateEntityFromAward(final AwardEntity awardEntity,
                                                        final AwardBidDto awardDto
    ) {
        awardEntity.setJsonData(jsonUtil.toJson(awardDto));
        return Optional.ofNullable(awardEntity);
    }





   /* private Optional<AwardEntity> updateEntity(final AwardEntity awardEntity,
                                               final AwardBidsResponseDto awardDto) {

        awardEntity.setStatus(awardDto.getStatus()
                                      .value());
        awardEntity.setJsonData(jsonUtil.toJson(awardDto));
        return Optional.ofNullable(awardEntity);
    }

    */

    private Optional<AwardEntity> getEntity(final SelectionsResponseAwardDto awardDto,
                                            final String ocId) {
        final AwardEntity awardEntity = new AwardEntity();
        awardEntity.setOcId(ocId);
        awardEntity.setAwardId(getUuid(awardDto));
        awardEntity.setStatus(awardDto.getStatus()
                                      .value());
        awardEntity.setJsonData(jsonUtil.toJson(awardDto));
        return Optional.ofNullable(awardEntity);
    }

    private UUID getUuid(final SelectionsResponseAwardDto awardDto) {
        final UUID awardId;
        if (Objects.isNull(awardDto.getId())) {
            awardId = UUIDs.timeBased();
            awardDto.setId(awardId.toString());
        } else {
            awardId = UUID.fromString(awardDto.getId());
        }
        return awardId;
    }

    private boolean isValidStatusDetail(Status awardStatusDetails) {
        List<Status> validIncomingStatuses = new ArrayList<Status>() {{
            add(Status.ACTIVE);
            add(Status.UNSUCCESSFUL);
        }};

        if (validIncomingStatuses.contains(awardStatusDetails)) {
            return true;
        } else {
            return false;
        }
    }

    private int isListAwardContainsOtherAwards(List<AwardBidDto> awardBidDtos) {

        for (int i = 0; i < awardBidDtos.size()-1; i++) {
            if (isNextAwardPendingAfterThisConsideration(awardBidDtos.get(i), awardBidDtos.get(i + 1))) {
                return i;
            }
        }
        return -1;
    }

    private List<AwardBidDto> getSurrentConsiderationAwardAndNextPendingAward(List<AwardBidDto> awardBidDtos,
                                                                              int index) {
        List<AwardBidDto> twoAwards = new ArrayList<>();
        twoAwards.add(awardBidDtos.get(index));
        twoAwards.add(awardBidDtos.get(index + 1));
        return twoAwards;
    }

    private boolean isNextAwardPendingAfterThisConsideration(AwardBidDto current, AwardBidDto next) {
        if ((current.getStatus() == Status.CONSIDERATION && current.getStatusDetails() != Status.UNSUCCESSFUL) ||
            (current.getStatusDetails() == Status.CONSIDERATION)&&current.getStatus()==Status.PENDING) {
            if (next.getStatus() == Status.PENDING && next.getStatusDetails() != Status.CONSIDERATION) {
                return true;
            }
        }
        return false;
    }

    }
