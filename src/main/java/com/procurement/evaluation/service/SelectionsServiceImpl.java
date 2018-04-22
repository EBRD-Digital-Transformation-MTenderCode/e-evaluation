package com.procurement.evaluation.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.evaluation.model.dto.bpe.ResponseDto;
import com.procurement.evaluation.model.dto.ocds.*;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseDto;
import com.procurement.evaluation.model.entity.AwardEntity;
import com.procurement.evaluation.repository.AwardRepository;
import com.procurement.evaluation.utils.DateUtil;
import com.procurement.evaluation.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import static com.procurement.evaluation.model.dto.ocds.AwardCriteria.PRICE_ONLY;
import static java.util.stream.Collectors.groupingBy;

@Service
public class SelectionsServiceImpl implements SelectionsService {

    private final RulesService rulesService;

    private final PeriodService periodService;

    private final AwardRepository awardRepository;

    private final DateUtil dateUtil;

    private final JsonUtil jsonUtil;

    public SelectionsServiceImpl(final RulesService rulesService,
                                 final PeriodService periodService,
                                 final AwardRepository awardRepository,
                                 final DateUtil dateUtil,
                                 final JsonUtil jsonUtil) {
        this.rulesService = rulesService;
        this.periodService = periodService;
        this.awardRepository = awardRepository;
        this.dateUtil = dateUtil;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public ResponseDto createAwards(String cpId,
                                    String stage,
                                    String owner,
                                    String country,
                                    String pmd,
                                    LocalDateTime startDate,
                                    SelectionsRequestDto dataDto) {

        final int minNumberOfBids = rulesService.getMinimumNumberOfBids(country, pmd);
        final List<String> relatedLotsFromBids = getRelatedLotsIdFromBids(dataDto);
        final List<String> lotsFromTender = getLotsFromTender(dataDto);
        final Map<String, Long> uniqueLots = getUniqueLots(relatedLotsFromBids);
        final List<String> successfulLots = getSuccessfulLots(uniqueLots, minNumberOfBids);
        final List<String> unsuccessfulLots = getUnsuccessfulLots(uniqueLots, minNumberOfBids);
        addUnsuccessfulLotsFromTender(successfulLots, unsuccessfulLots, lotsFromTender);
        final List<Bid> successfulBids = getSuccessfulBids(dataDto, successfulLots);
        final List<Award> awards = getSuccessfulAwards(successfulBids);
        setAwardIds(awards);
        sortSuccessfulAwards(awards, AwardCriteria.fromValue(dataDto.getAwardCriteria()));
        awards.addAll(getUnsuccessfulAwards(unsuccessfulLots));
        /**save evaluation period*/
        final Period periodDto = periodService.saveStartOfPeriod(cpId, stage, startDate);
        /**save awards to DB*/
        saveAwards(awards, cpId, owner, stage);
        return new ResponseDto<>(true, null,
                new SelectionsResponseDto(periodDto, awards, fillLotDto(unsuccessfulLots))
        );
    }

    private void addUnsuccessfulLotsFromTender(final List<String> successfulLots,
                                               final List<String> unsuccessfulLots,
                                               final List<String> lotsFromTender) {
        lotsFromTender.stream()
                .filter(lot -> !successfulLots.contains(lot) && !unsuccessfulLots.contains(lot))
                .forEach(unsuccessfulLots::add);
    }


    private List<String> getRelatedLotsIdFromBids(final SelectionsRequestDto dataDto) {

        return dataDto.getBids()
                .stream()
                .flatMap(bidDto -> bidDto.getRelatedLots().stream())
                .collect(Collectors.toList());
    }

    private List<String> getLotsFromTender(final SelectionsRequestDto dataDto) {

        return dataDto.getLots()
                .stream()
                .map(Lot::getId)
                .collect(Collectors.toList());
    }

    private Map<String, Long> getUniqueLots(final List<String> lots) {
        return lots.stream()
                .collect(groupingBy(Function.identity(), Collectors.counting()));
    }

    private List<String> getSuccessfulLots(final Map<String, Long> uniqueLots,
                                           final int minNumberOfBids) {

        return uniqueLots.entrySet()
                .stream()
                .filter(map -> map.getValue() >= minNumberOfBids)
                .map(map -> map.getKey())
                .collect(Collectors.toList());
    }

    private List<String> getUnsuccessfulLots(final Map<String, Long> uniqueLots,
                                             final int minNumberOfBids) {

        return uniqueLots.entrySet()
                .stream()
                .filter(map -> map.getValue() < minNumberOfBids)
                .map(map -> map.getKey())
                .collect(Collectors.toList());
    }

    private List<Bid> getSuccessfulBids(final SelectionsRequestDto dataDto,
                                        final List<String> successfulLots) {
        final List<Bid> bids = new ArrayList<>();
        dataDto.getBids().forEach(bid ->
                bid.getRelatedLots()
                        .stream()
                        .filter(successfulLots::contains)
                        .map(lot -> bid)
                        .forEach(bids::add));
        return bids;
    }

    private List<Lot> fillLotDto(final List<String> lots) {
        return lots.stream().map(Lot::new).collect(Collectors.toList());
    }

    private String generateAwardId() {
        return UUIDs.timeBased().toString();
    }

    private void setAwardIds(final List<Award> awards) {
        awards.forEach(award -> award.setId(generateAwardId()));
    }

    private List<Award> getSuccessfulAwards(final List<Bid> successfulBids) {
        return successfulBids.stream().map(bid ->
                new Award(
                        UUIDs.timeBased().toString(),
                        null,
                        dateUtil.localNowUTC(),
                        "",
                        Status.PENDING,
                        Status.EMPTY,
                        bid.getValue(),
                        bid.getRelatedLots(),
                        bid.getId(),
                        bid.getTenderers(),
                        null
                )
        ).collect(Collectors.toList());
    }

    private List<Award> getUnsuccessfulAwards(final List<String> unSuccessfulLots) {
        return unSuccessfulLots.stream().map(lot ->
                new Award(
                        UUIDs.timeBased().toString(),
                        null,
                        dateUtil.localNowUTC(),
                        "",
                        Status.UNSUCCESSFUL,
                        Status.EMPTY,
                        null,
                        Collections.singletonList(lot),
                        null,
                        null,
                        null)
        ).collect(Collectors.toList());
    }

    private void sortSuccessfulAwards(final List<Award> awards, final AwardCriteria awardCriteria) {
        switch(awardCriteria) {
            case PRICE_ONLY:
                awards.sort(new SortedByValue());
                awards.get(0).setStatusDetails(Status.CONSIDERATION);
                break;
            case COST_ONLY:
                break;
            case QUALITY_ONLY:
                break;
            case RATED_CRITERIA:
                break;
            case LOWEST_COST:
                break;
            case BEST_PROPOSAL:
                break;
            case BEST_VALUE_TO_GOVERNMENT:
                break;
            case SINGLE_BID_ONLY:
                break;
        }
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

    private void saveAwards(final List<Award> awards,
                            final String ocId,
                            final String owner,
                            final String stage) {
        awards.forEach(awardDto -> {
            final AwardEntity entity = awardRepository.save(getEntity(awardDto, ocId, owner, stage));
            awardDto.setToken(entity.getToken().toString());
        });
    }

    private AwardEntity getEntity(final Award award,
                                  final String cpId,
                                  final String owner,
                                  final String stage) {
        final AwardEntity entity = new AwardEntity();
        entity.setCpId(cpId);
        entity.setStage(stage);
        entity.setToken(UUIDs.random());
        entity.setStatus(award.getStatus().value());
        entity.setStatusDetails(award.getStatusDetails().value());
        entity.setOwner(owner);
        entity.setJsonData(jsonUtil.toJson(award));
        return entity;
    }

}
