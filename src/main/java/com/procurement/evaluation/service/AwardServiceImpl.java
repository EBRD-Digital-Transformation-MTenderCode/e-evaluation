package com.procurement.evaluation.service;

import com.procurement.evaluation.exception.ErrorException;
import com.procurement.evaluation.exception.ErrorType;
import com.procurement.evaluation.model.dto.AwardsResponseDto;
import com.procurement.evaluation.model.dto.UpdateAwardRequestDto;
import com.procurement.evaluation.model.dto.UpdateAwardResponseDto;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.ocds.Award;
import com.procurement.evaluation.model.dto.ocds.Lot;
import com.procurement.evaluation.model.dto.ocds.Period;
import com.procurement.evaluation.model.dto.ocds.Status;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
public class AwardServiceImpl implements AwardService {

    private final AwardRepository awardRepository;
    private final JsonUtil jsonUtil;
    private final PeriodService periodService;

    public AwardServiceImpl(final AwardRepository awardRepository,
                            final JsonUtil jsonUtil,
                            final PeriodService periodService) {
        this.awardRepository = awardRepository;
        this.jsonUtil = jsonUtil;
        this.periodService = periodService;
    }

    @Override
    public ResponseDto updateAward(final String cpId,
                                   final String stage,
                                   final String token,
                                   final String owner,
                                   final LocalDateTime dateTime,
                                   final UpdateAwardRequestDto dataDto) {
        final Award awardDto = dataDto.getAward();
        switch (awardDto.getStatusDetails()) {
            case ACTIVE:
                final AwardEntity entity = Optional.ofNullable(awardRepository.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token)))
                        .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
                if (!entity.getOwner().equals(owner)) throw new ErrorException(ErrorType.INVALID_OWNER);
                final Award award = jsonUtil.toObject(Award.class, entity.getJsonData());
                updateActiveAward(award, awardDto, dateTime);
                entity.setJsonData(jsonUtil.toJson(award));
                awardRepository.save(entity);
                return getResponseDtoForActiveAward(award);
            case UNSUCCESSFUL:
                final List<AwardEntity> entities = Optional.ofNullable(awardRepository.getAllByCpidAndStage(cpId, stage))
                        .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
                Map<Award, AwardEntity> awardsFromEntities = getMapAwardsFromEntities(entities);
                return updateUnsuccessfulAward(awardDto, awardsFromEntities, dateTime);
            default:
                throw new ErrorException(ErrorType.INVALID_STATUS_DETAILS);
        }
    }

    @Override
    public ResponseDto getAwards(final String cpId,
                                 final String stage,
                                 final String country,
                                 final String pmd) {
        final List<AwardEntity> awardEntities = Optional.ofNullable(awardRepository.getAllByCpidAndStage(cpId, stage))
                .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
        final List<Award> activeAwards = getActiveAwardsFromEntities(awardEntities);
        return new ResponseDto<>(true, null, new AwardsResponseDto(activeAwards, null, null));
    }

    @Override
    public ResponseDto endAwardPeriod(final String cpId,
                                      final String stage,
                                      final String country,
                                      final String pmd,
                                      final LocalDateTime endPeriod) {
        final Period awardPeriod = periodService.saveEndOfPeriod(cpId, stage, endPeriod);
        final List<AwardEntity> awardEntities = awardRepository.getAllByCpidAndStage(cpId, stage);
        if (awardEntities.isEmpty()) throw new ErrorException(ErrorType.DATA_NOT_FOUND);
        final List<Award> awards = getAwardsFromEntities(awardEntities);
        setAwardsStatusFromStatusDetails(awards, endPeriod);
        final List<Lot> unsuccessfulLots = getUnsuccessfulLotsFromAwards(awards, country, pmd);
        return new ResponseDto<>(true, null, new AwardsResponseDto(awards, awardPeriod, unsuccessfulLots));
    }

    private ResponseDto updateUnsuccessfulAward(final Award awardDto,
                                                final Map<Award, AwardEntity> awardsFromEntities,
                                                final LocalDateTime dateTime) {
        // unsuccessful Award
        final Award updatableAward = awardsFromEntities.keySet().stream()
                .filter(a -> a.getId().equals(awardDto.getId())).findFirst()
                .orElseThrow(() -> new ErrorException(ErrorType.DATA_NOT_FOUND));
        if (!updatableAward.getStatusDetails().equals(Status.CONSIDERATION))
            throw new ErrorException(ErrorType.INVALID_STATUS_DETAILS);
        AwardEntity updatedAwardEntity = awardsFromEntities.get(updatableAward);
        if (awardDto.getDescription() != null) updatableAward.setDescription(awardDto.getDescription());
        if (awardDto.getStatusDetails() != null) updatableAward.setStatusDetails(awardDto.getStatusDetails());
        if (awardDto.getDocuments() != null) updatableAward.setDocuments(awardDto.getDocuments());
        updatableAward.setDate(dateTime);
        updatedAwardEntity.setJsonData(jsonUtil.toJson(updatableAward));
        awardRepository.save(updatedAwardEntity);
        // next Award
        Award nextAwardByLot = null;
        Set<Award> awards = awardsFromEntities.keySet().stream()
                .filter(a -> a.getRelatedLots().equals(updatableAward.getRelatedLots()))
                .sorted(new SortedByValue()).collect(toSet());
        if (awards.size() > 1) {
            nextAwardByLot = awards.stream()
                    .filter(a -> !a.getId().equals(updatableAward.getId()))
                    .findFirst().orElse(null);
            if (nextAwardByLot != null) {
                AwardEntity nextAwardByLotEntity = awardsFromEntities.get(nextAwardByLot);
                nextAwardByLot.setStatusDetails(Status.CONSIDERATION);
                nextAwardByLot.setDate(dateTime);
                nextAwardByLotEntity.setStatusDetails(nextAwardByLot.getStatusDetails().value());
                nextAwardByLotEntity.setJsonData(jsonUtil.toJson(updatableAward));
                awardRepository.save(nextAwardByLotEntity);
            }
        }
        return new ResponseDto<>(true, null,
                new UpdateAwardResponseDto(
                        updatableAward,
                        nextAwardByLot,
                        updatableAward.getRelatedBid(),
                        null));
    }

    private void updateActiveAward(final Award award,
                                   final Award awardDto,
                                   final LocalDateTime dateTime) {
        if (!award.getStatusDetails().equals(Status.CONSIDERATION))
            throw new ErrorException(ErrorType.INVALID_STATUS_DETAILS);
        if (Objects.nonNull(awardDto.getDescription())) award.setDescription(awardDto.getDescription());
        if (Objects.nonNull(awardDto.getStatusDetails())) award.setStatusDetails(awardDto.getStatusDetails());
        if (Objects.nonNull(awardDto.getDocuments())) award.setDocuments(awardDto.getDocuments());
        award.setDate(dateTime);
    }

    private List<Lot> getUnsuccessfulLotsFromAwards(final List<Award> awards,
                                                    final String country,
                                                    final String pmd) {
        final List<String> unsuccessfulRelatedLotsFromAward = getUnsuccessfulRelatedLotsIdFromAwards(awards);
        final Map<String, Long> uniqueLots = getUniqueLots(unsuccessfulRelatedLotsFromAward);
        final List<String> unsuccessfulLots = getUnsuccessfulLots(uniqueLots);
        return unsuccessfulLots.stream().map(Lot::new).collect(Collectors.toList());
    }

    private List<String> getUnsuccessfulRelatedLotsIdFromAwards(final List<Award> awards) {
        return awards.stream()
                .filter(award ->
                        (award.getStatus().equals(Status.UNSUCCESSFUL) && award.getStatusDetails().equals(Status.EMPTY)))
                .flatMap(award -> award.getRelatedLots().stream())
                .collect(Collectors.toList());
    }

    private Map<String, Long> getUniqueLots(final List<String> lots) {
        return lots.stream()
                .collect(groupingBy(Function.identity(), Collectors.counting()));
    }

    private List<String> getUnsuccessfulLots(final Map<String, Long> uniqueLots) {
        return uniqueLots.entrySet()
                .stream()
                .filter(map -> map.getValue() > 0)
                .map(map -> map.getKey())
                .collect(Collectors.toList());
    }

    private List<Award> getAwardsFromEntities(final List<AwardEntity> awardEntities) {
        return awardEntities.stream()
                .map(e -> jsonUtil.toObject(Award.class, e.getJsonData()))
                .collect(Collectors.toList());
    }

    private List<Award> getActiveAwardsFromEntities(final List<AwardEntity> awardEntities) {
        return awardEntities.stream()
                .map(e -> jsonUtil.toObject(Award.class, e.getJsonData()))
                .filter(award -> (award.getStatus().equals(Status.ACTIVE) && award.getStatusDetails().equals(Status.EMPTY)))
                .collect(Collectors.toList());
    }

    private void setAwardsStatusFromStatusDetails(final List<Award> awards, final LocalDateTime endPeriod) {
        awards.forEach(a -> {
            if (a.getStatusDetails() != Status.EMPTY) {
                a.setDate(endPeriod);
                a.setStatus(a.getStatusDetails());
                a.setStatusDetails(Status.EMPTY);
            }
        });
    }

    private Map<Award, AwardEntity> getMapAwardsFromEntities(final List<AwardEntity> awardEntities) {
        final Map<Award, AwardEntity> awardsFromEntities = new HashMap<>();
        awardEntities.forEach(e -> {
            final Award award = jsonUtil.toObject(Award.class, e.getJsonData());
            awardsFromEntities.put(award, e);
        });
        return awardsFromEntities;
    }

    private ResponseDto getResponseDtoForActiveAward(final Award award) {
        return new ResponseDto<>(true, null,
                new UpdateAwardResponseDto(
                        award,
                        null,
                        award.getRelatedBid(),
                        award.getRelatedLots().get(0))
        );
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
}
