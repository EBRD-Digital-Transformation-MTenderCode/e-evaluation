package com.procurement.evaluation.service;

import com.procurement.evaluation.exception.ErrorException;
import com.procurement.evaluation.exception.ErrorType;
import com.procurement.evaluation.model.dto.UpdateAwardRequestDto;
import com.procurement.evaluation.model.dto.UpdateAwardResponseDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.ocds.Award;
import com.procurement.evaluation.model.dto.ocds.Status;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.utils.DateUtil;
import com.procurement.evaluation.utils.JsonUtil;
import java.util.*;
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
        switch (awardDto.getStatusDetails()) {
            case ACTIVE:
                final AwardEntity entity = Optional.ofNullable(awardRepository.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token)))
                        .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
                if (!entity.getOwner().equals(owner)) throw new ErrorException(ErrorType.INVALID_OWNER);
                final Award award = jsonUtil.toObject(Award.class, entity.getJsonData());
                if (Objects.nonNull(awardDto.getDescription())) award.setDescription(awardDto.getDescription());
                if (Objects.nonNull(awardDto.getStatusDetails())) award.setStatusDetails(awardDto.getStatusDetails());
                if (Objects.nonNull(awardDto.getDocuments())) award.setDocuments(awardDto.getDocuments());
                award.setDate(dateUtil.localNowUTC());
                entity.setJsonData(jsonUtil.toJson(award));
                awardRepository.save(entity);
                return getResponseDtoForAward(award);
            case UNSUCCESSFUL:
                final List<AwardEntity> entities = Optional.ofNullable(awardRepository.getAllByCpidAndStage(cpId, stage))
                        .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));

                Map<Award, AwardEntity> awardsFromEntities = getAwardsFromEntities(entities);
                final List<Award> updatedAwards = new ArrayList<>();
                // UNSUCCESSFUL AWARD
                final Award updatableAward = Optional.of(
                        awardsFromEntities.keySet().stream()
                                .filter(a -> a.getId().equals(awardDto.getId()))
                                .findFirst()
                                .get())
                        .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
                AwardEntity updatedAwardEntity = awardsFromEntities.get(updatableAward);
                if (awardDto.getDescription() != null) updatableAward.setDescription(awardDto.getDescription());
                if (awardDto.getStatusDetails() != null) updatableAward.setStatusDetails(awardDto.getStatusDetails());
                if (awardDto.getDocuments() != null) updatableAward.setDocuments(awardDto.getDocuments());
                updatedAwardEntity.setJsonData(jsonUtil.toJson(updatableAward));
                awardRepository.save(updatedAwardEntity);
                updatedAwards.add(updatableAward);

                // NEXT AWARD BY LOT
                Award nextAwardByLot = awardsFromEntities.keySet().stream().filter(a -> a.getRelatedLots().equals(updatableAward
                        .getRelatedLots())).sorted(new SortedByValue()).findFirst().get();
                if (nextAwardByLot != null) {
                    AwardEntity nextAwardByLotEntity = awardsFromEntities.get(nextAwardByLot);
                    nextAwardByLot.setStatusDetails(Status.CONSIDERATION);
                    nextAwardByLotEntity.setJsonData(jsonUtil.toJson(updatableAward));
                    awardRepository.save(nextAwardByLotEntity);
                    updatedAwards.add(nextAwardByLot);
                  }
                return getResponseDtoForAwards(updatedAwards);

            default:
                throw new ErrorException(ErrorType.INVALID_STATUS_DETAILS);
        }

    }

    private Map<Award, AwardEntity> getAwardsFromEntities(final List<AwardEntity> awardEntities) {
        final Map<Award, AwardEntity> awardsFromEntities = new HashMap<>();
        awardEntities.forEach(e -> {
            final Award award = jsonUtil.toObject(Award.class, e.getJsonData());
            awardsFromEntities.put(award, e);
        });
        return awardsFromEntities;
    }

    private ResponseDto getResponseDtoForAward(final Award award) {
        return new ResponseDto<>(true, null,
                new UpdateAwardResponseDto(
                        Collections.singletonList(award),
                        award.getRelatedBid(), award
                        .getRelatedLots().get(0))
        );
    }

    private ResponseDto getResponseDtoForAwards(final List<Award> awards) {
        return new ResponseDto<>(true, null,
                new UpdateAwardResponseDto(
                        awards, awards.get(0).getRelatedBid(),
                        awards.get(0).getRelatedLots().get(0)));
    }

    private class SortedByValue implements Comparator<Award> {

        public int compare(final Award obj1, final Award obj2) {
            final double val1 = obj1.getValue().getAmount();
            final double val2 = obj2.getValue().getAmount();
            if (val1 > val2) {
                return 1;
            } else if (val1 < val2) {
                return -1;
            } else {
                return 0;
            }
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
