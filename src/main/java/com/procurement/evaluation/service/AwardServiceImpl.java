package com.procurement.evaluation.service;

import com.procurement.evaluation.exception.ErrorException;
import com.procurement.evaluation.exception.ErrorType;
import com.procurement.evaluation.model.dto.UpdateAwardRequestDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.ocds.Award;
import com.procurement.evaluation.model.dto.ocds.Status;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.utils.DateUtil;
import com.procurement.evaluation.utils.JsonUtil;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

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
    public ResponseDto updateAward(final String cpId,
                                   final String stage,
                                   final String token,
                                   final String owner,
                                   final UpdateAwardRequestDto dataDto) {
        final Award awardDto = dataDto.getAward();
        /*active*/
        if (awardDto.getStatusDetails().equals(Status.ACTIVE)) {
            final AwardEntity awardEntity = Optional.ofNullable(awardRepository.findAwardEntity(cpId, stage, UUID.fromString(token)))
                    .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
            if (!awardEntity.getOwner().equals(owner)) throw new ErrorException(ErrorType.INVALID_OWNER);

            final Award award = jsonUtil.toObject(Award.class, awardEntity.getJsonData());
            if (Objects.nonNull(awardDto.getDescription())) award.setDescription(awardDto.getDescription());
            if (Objects.nonNull(awardDto.getStatusDetails())) award.setStatusDetails(awardDto.getStatusDetails());
            if (Objects.nonNull(awardDto.getDocuments())) award.setDocuments(awardDto.getDocuments());
            award.setDate(dateUtil.localNowUTC());
            awardEntity.setJsonData(jsonUtil.toJson(award));
            awardRepository.save(awardEntity);
            return new ResponseDto<>(true, null, new UpdateAwardRequestDto(award));
        }

    }

//    @Override
//    public List<AwardBidRSDto> getAwardsDtoFromEntity(final List<AwardEntity> awardPeriodEntities) {
//        final List<AwardBidRSDto> awardBidsResponseDtos = new ArrayList<>();
//
//        for (int i = 0; i < awardPeriodEntities.size(); i++) {
//
//            final AwardBidRSDto periodResponseAwardDto = getAwardDtoFromEntity(awardPeriodEntities.get(i));
//
//            awardBidsResponseDtos.add(periodResponseAwardDto);
//        }
//
//        return awardBidsResponseDtos;
//    }
//
//    private AwardBidRSDto getAwardDtoFromEntity(final AwardEntity entity) {
//        return jsonUtil.toObject(AwardBidRSDto.class, entity.getJsonData());
//    }
//
//    private Optional<AwardEntity> updateEntityFromAward(final AwardEntity awardEntity,
//                                                        final AwardBidRSDto awardDto
//    ) {
//        awardEntity.setStatusDetails(awardDto.getStatusDetails()
//                .value());
//        awardEntity.setJsonData(jsonUtil.toJson(awardDto));
//        return Optional.ofNullable(awardEntity);
//    }
//
//    private Optional<AwardEntity> getEntity(final Award awardDto,
//                                            final String ocId,
//                                            final String owner) {
//        final AwardEntity awardEntity = new AwardEntity();
//        awardEntity.setOcId(ocId);
//        awardEntity.setAwardId(getUuid(awardDto));
//        awardEntity.setStatus(awardDto.getStatus()
//                .value());
//        awardEntity.setOwner(owner);
//        awardEntity.setJsonData(jsonUtil.toJson(awardDto));
//        return Optional.ofNullable(awardEntity);
//    }
//
//    private UUID getUuid(final Award awardDto) {
//        final UUID awardId;
//        if (Objects.isNull(awardDto.getId())) {
//            awardId = UUIDs.timeBased();
//            awardDto.setId(awardId.toString());
//        } else {
//            awardId = UUID.fromString(awardDto.getId());
//        }
//        return awardId;
//    }
//
//    private boolean isValidStatusDetail(final Status awardStatusDetails) {
//        final List<Status> validIncomingStatuses = new ArrayList<>();
//        validIncomingStatuses.add(Status.ACTIVE);
//        validIncomingStatuses.add(Status.UNSUCCESSFUL);
//
//        if (validIncomingStatuses.contains(awardStatusDetails)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private int isListAwardContainsOtherAwards(final List<AwardEntity> awardBidDtos) {
//
//        for (int i = 0; i < awardBidDtos.size() - 1; i++) {
//            if (isNextAwardPendingAfterThisConsideration(awardBidDtos.get(i), awardBidDtos.get(i + 1))) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    private List<AwardEntity> getSurrentConsiderationAwardAndNextPendingAward(final List<AwardEntity> entities,
//                                                                              final int index) {
//        final List<AwardEntity> twoAwards = new ArrayList<>();
//        twoAwards.add(entities.get(index));
//        twoAwards.add(entities.get(index + 1));
//        return twoAwards;
//    }
//
//    private boolean isNextAwardPendingAfterThisConsideration(final AwardEntity current, final AwardEntity next) {
//        if ((current.getStatus() == Status.CONSIDERATION.value() && current.getStatusDetails() != Status.UNSUCCESSFUL
//                .value()) ||
//                (current.getStatusDetails() == Status.CONSIDERATION.value()) && current.getStatus() == Status.PENDING
//                        .value()) {
//            if (next.getStatus() == Status.PENDING.value() && next.getStatusDetails() != Status.CONSIDERATION.value()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private Boolean isTokenValid(final String cpId, final String owner, final AwardBidRQDto awardBidDto) {
//        final AwardEntity awardEntity = awardRepository.findAwardEntity(cpId, UUID.fromString(awardBidDto.getId()));
//
//        if (!awardEntity.getOwner()
//                .equals(owner)) {
//            return false;
//        }
//
//        return true;
//    }
}
