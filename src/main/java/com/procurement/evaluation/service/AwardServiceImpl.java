package com.procurement.evaluation.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.Status;
import com.procurement.evaluation.model.dto.award.AwardBidRQDto;
import com.procurement.evaluation.model.dto.award.AwardBidRSDto;
import com.procurement.evaluation.model.dto.award.AwardRequestDto;
import com.procurement.evaluation.model.dto.award.AwardResponseDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
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
    public ResponseDto updateAwardDto(final AwardRequestDto awardDto) {
        final AwardBidRQDto awardBidDto = awardDto.getAwards();
        final Status awardStatusDetails = awardBidDto.getStatusDetails();
        final LocalDateTime localDateTime = dateUtil.getNowUTC();
        final String cpId = awardDto.getCpId();
        final AwardPeriodDto awardPeriodDto;
        final AwardBidRSDto awardBidResponse;

        final List<AwardBidRSDto> awardBidResponseDtos = new ArrayList<AwardBidRSDto>();

        final AwardResponseDto responseDto = new AwardResponseDto(null, null);

        final Boolean isTokenValid = isTokenValid(cpId, awardDto.getOwner(), awardBidDto);

        if (isTokenValid) {
            if (isValidStatusDetail(awardStatusDetails)) {
                switch (awardStatusDetails) {
                    case ACTIVE:

                        AwardEntity awardEntity = awardRepository.findAwardEntity(cpId, UUID.fromString(awardBidDto
                                                                                                            .getId()));

                        awardPeriodDto = periodService.saveEndOfPeriod(cpId, localDateTime);

                        AwardBidRSDto awardBidDtofromEntity = getAwardDtoFromEntity(awardEntity);

                        awardBidDtofromEntity.setStatusDetails(awardBidDto.getStatusDetails());

                        updateAward(awardBidDtofromEntity, cpId);

                        awardBidResponseDtos.add(awardBidDtofromEntity);

                        responseDto.setAwardPeriod(awardPeriodDto);
                        responseDto.setAwards(awardBidResponseDtos);
                        break;

                    case UNSUCCESSFUL:

                        final List<AwardEntity> awardEntities = awardRepository.selectAwardsEntityByOcid(cpId);

                        final int indexRequriedAward = isListAwardContainsOtherAwards(awardEntities);

                        if (indexRequriedAward >= 0) {

                            final List<AwardEntity> awardEntitiesConsiderationPending =
                                getSurrentConsiderationAwardAndNextPendingAward(awardEntities, indexRequriedAward);

                            awardEntitiesConsiderationPending.get(0)
                                                             .setStatusDetails(awardStatusDetails.value());
                            awardEntitiesConsiderationPending.get(1)
                                                             .setStatusDetails(Status.CONSIDERATION.value());

                            awardPeriodDto = periodService.saveStartOfPeriod(cpId, localDateTime);

                            responseDto.setAwardPeriod(awardPeriodDto);
                            final List<AwardBidRSDto> awardBidDtosConsiderationPending =
                                getAwardsDtoFromEntity(awardEntitiesConsiderationPending);
                            responseDto.setAwards(awardBidDtosConsiderationPending);
                        } else {

                            awardEntity = awardRepository.findAwardEntity(cpId, UUID.fromString(awardBidDto.getId()));
                            awardPeriodDto = periodService.saveEndOfPeriod(cpId, localDateTime);
                            awardBidDtofromEntity = getAwardDtoFromEntity(awardEntity);

                            awardBidDtofromEntity.setStatusDetails(awardBidDto.getStatusDetails());

                            updateAward(awardBidDtofromEntity, cpId);

                            awardBidResponseDtos.add(awardBidDtofromEntity);

                            responseDto.setAwardPeriod(awardPeriodDto);
                            responseDto.setAwards(awardBidResponseDtos);
                        }
                        break;
                }
                return new ResponseDto(true, null, responseDto);
            } else {
                final List<ResponseDto.ResponseDetailsDto> responseDetails = new ArrayList<>();
                responseDetails.add(new ResponseDto.ResponseDetailsDto("code", "not valid awards"));
                return new ResponseDto(false, responseDetails, null);
            }
        } else {
            final List<ResponseDto.ResponseDetailsDto> responseDetails = new ArrayList<>();
            responseDetails.add(new ResponseDto.ResponseDetailsDto("code", "invalid token"));
            return new ResponseDto(false, responseDetails, null);
        }
    }

    @Override
    @Transactional
    public void saveAwards(final List<SelectionsResponseAwardDto> awards,
                           final String ocId, final String stage, final String owner) {
        for (final SelectionsResponseAwardDto awardDto : awards) {
            getEntity(awardDto, ocId, owner)
                .ifPresent(awardRepository::save);
        }
    }

    @Override
    public AwardEntity updateAward(final AwardBidRSDto award, final String ocId) {

        final AwardEntity awardEntity = awardRepository.findAwardEntity(ocId, UUID.fromString(award.getId()));
        updateEntityFromAward(awardEntity, award)
            .ifPresent(awardRepository::save);
        return awardEntity;
    }

    @Override
    public List<AwardBidRSDto> getAwardsDtoFromEntity(final List<AwardEntity> awardPeriodEntities) {
        final List<AwardBidRSDto> awardBidsResponseDtos = new ArrayList<>();

        for (int i = 0; i < awardPeriodEntities.size(); i++) {

            final AwardBidRSDto periodResponseAwardDto = getAwardDtoFromEntity(awardPeriodEntities.get(i));

            awardBidsResponseDtos.add(periodResponseAwardDto);
        }

        return awardBidsResponseDtos;
    }

    private AwardBidRSDto getAwardDtoFromEntity(final AwardEntity entity) {
        return jsonUtil.toObject(AwardBidRSDto.class, entity.getJsonData());
    }

    private Optional<AwardEntity> updateEntityFromAward(final AwardEntity awardEntity,
                                                        final AwardBidRSDto awardDto
    ) {
        awardEntity.setStatusDetails(awardDto.getStatusDetails()
                                             .value());
        awardEntity.setJsonData(jsonUtil.toJson(awardDto));
        return Optional.ofNullable(awardEntity);
    }

    private Optional<AwardEntity> getEntity(final SelectionsResponseAwardDto awardDto,
                                            final String ocId,
                                            final String owner) {
        final AwardEntity awardEntity = new AwardEntity();
        awardEntity.setOcId(ocId);
        awardEntity.setAwardId(getUuid(awardDto));
        awardEntity.setStatus(awardDto.getStatus()
                                      .value());
        awardEntity.setOwner(owner);
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

    private boolean isValidStatusDetail(final Status awardStatusDetails) {
        final List<Status> validIncomingStatuses = new ArrayList<>();
        validIncomingStatuses.add(Status.ACTIVE);
        validIncomingStatuses.add(Status.UNSUCCESSFUL);

        if (validIncomingStatuses.contains(awardStatusDetails)) {
            return true;
        } else {
            return false;
        }
    }

    private int isListAwardContainsOtherAwards(final List<AwardEntity> awardBidDtos) {

        for (int i = 0; i < awardBidDtos.size() - 1; i++) {
            if (isNextAwardPendingAfterThisConsideration(awardBidDtos.get(i), awardBidDtos.get(i + 1))) {
                return i;
            }
        }
        return -1;
    }

    private List<AwardEntity> getSurrentConsiderationAwardAndNextPendingAward(final List<AwardEntity> entities,
                                                                              final int index) {
        final List<AwardEntity> twoAwards = new ArrayList<>();
        twoAwards.add(entities.get(index));
        twoAwards.add(entities.get(index + 1));
        return twoAwards;
    }

    private boolean isNextAwardPendingAfterThisConsideration(final AwardEntity current, final AwardEntity next) {
        if ((current.getStatus() == Status.CONSIDERATION.value() && current.getStatusDetails() != Status.UNSUCCESSFUL
            .value()) ||
            (current.getStatusDetails() == Status.CONSIDERATION.value()) && current.getStatus() == Status.PENDING
                .value()) {
            if (next.getStatus() == Status.PENDING.value() && next.getStatusDetails() != Status.CONSIDERATION.value()) {
                return true;
            }
        }
        return false;
    }

    private Boolean isTokenValid(final String cpId, final String owner, final AwardBidRQDto awardBidDto) {
        final AwardEntity awardEntity = awardRepository.findAwardEntity(cpId, UUID.fromString(awardBidDto.getId()));

        if (!awardEntity.getOwner()
                        .equals(owner)) {
            return false;
        }

        return true;
    }
}
