package com.procurement.evaluation.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.LotDto;
import com.procurement.evaluation.model.dto.Status;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestBidDto;
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseAwardDto;
import com.procurement.evaluation.model.dto.selections.SelectionsResponseDto;
import com.procurement.evaluation.utils.DateUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.groupingBy;

@Service
public class SelectionsServiceImpl implements SelectionsService {

    private final RulesService rulesService;

    private final PeriodService periodService;

    private final AwardService awardService;

    private final DateUtil dateUtil;

    public SelectionsServiceImpl(final RulesService rulesService,
                                 final PeriodService periodService,
                                 final AwardService awardService,
                                 final DateUtil dateUtil) {
        this.rulesService = rulesService;
        this.periodService = periodService;
        this.awardService = awardService;
        this.dateUtil = dateUtil;
    }

    @Override
    public SelectionsResponseDto getAwards(final SelectionsRequestDto dataDto) {

        final int minNumberOfBids = getBidsRule(dataDto);

        final String ocid = dataDto.getOcid();

        final LocalDateTime addedDate = dateUtil.getNowUTC();

        final List<String> relatedLotsFromBids = getRelatedLotsIdFromBids(dataDto);

        final List<String> lotsFromTender = getLotsFromTender(dataDto);

        final Map<String, Long> uniqueLots = getUniqueLots(relatedLotsFromBids);

        final List<String> successfulLots = getSuccessfulLots(uniqueLots, minNumberOfBids);

        final List<String> unsuccessfulLots = getUnsuccessfulLots(uniqueLots, minNumberOfBids);

        lotsFromTender.stream()
                      .filter(lotFromTender -> !unsuccessfulLots.contains(lotFromTender))
                      .sorted()
                      .forEachOrdered(unsuccessfulLots::add);

        final List<SelectionsRequestBidDto> successfulBids = getSuccessfulBids(dataDto, successfulLots);

        final List<SelectionsResponseAwardDto> awards = getSuccessfulAwards(successfulBids);

        sortSuccessfulAwards(awards);

        setAwardIds(awards);

        setStatusConsideration(awards);

        awards.addAll(getUnsuccessfulAwards(unsuccessfulLots));


        final AwardPeriodDto periodDto = periodService.saveStartOfPeriod(dataDto.getOcid(), addedDate);


        awardService.saveAwards(awards, ocid);

        final SelectionsResponseDto responseDto = new SelectionsResponseDto(ocid,
                                                                            "rationale",
                                                                            periodDto,
                                                                            awards,
                                                                            fillLotDto(unsuccessfulLots));

        return responseDto;
    }

    private List<String> getRelatedLotsIdFromBids(final SelectionsRequestDto dataDto) {

        return dataDto.getBids()
                      .stream()
                      .flatMap(bidDto -> bidDto.getRelatedLots()
                                               .stream())
                      .collect(Collectors.toList());
    }

    private List<String> getLotsFromTender(final SelectionsRequestDto dataDto) {

        return dataDto.getLots()
                      .stream()
                      .map(LotDto::getId)
                      .collect(Collectors.toList());
    }

    private Map<String, Long> getUniqueLots(final List<String> bids) {
        final Map<String, Long> uniqueLots = bids.stream()
                                                 .collect(groupingBy(Function.identity(), Collectors.counting()));
        return uniqueLots;
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

    private int getBidsRule(final SelectionsRequestDto dataDto) {
        return rulesService.getMinimumNumberOfBids(dataDto.getCountry(),
                                                   dataDto.getProcurementMethodDetails()
        );
    }

    private List<SelectionsRequestBidDto> getSuccessfulBids(final SelectionsRequestDto dataDto,
                                                            final List<String> successfulLots) {
        final List<SelectionsRequestBidDto> bids = new ArrayList<>();
        for (int i = 0; i < dataDto.getBids()
                                   .size(); i++) {
            for (int j = 0; j < dataDto.getBids()
                                       .get(i)
                                       .getRelatedLots()
                                       .size(); j++) {
                if (successfulLots.contains(dataDto.getBids()
                                                   .get(i)
                                                   .getRelatedLots()
                                                   .get(j))) {
                    bids.add(dataDto.getBids()
                                    .get(i));
                }
            }
        }
        return bids;
    }

    private List<LotDto> fillLotDto(final List<String> lots) {
        final List<LotDto> lotsDto = new ArrayList<>();
        for (int i = 0; i < lots.size(); i++) {
            lotsDto.add(new LotDto(lots.get(i)));
        }
        return lotsDto;
    }

    private String generateAwardId() {
        return UUIDs.timeBased()
                    .toString();
    }

    private void setAwardIds(List<SelectionsResponseAwardDto> awards){
        for (int i = 0; i < awards.size(); i++) {
            awards.get(i).setId(generateAwardId());
        }
    }

    private List<SelectionsResponseAwardDto> getSuccessfulAwards(final List<SelectionsRequestBidDto> successfulBids) {
        final List<SelectionsResponseAwardDto> awards = new ArrayList<>();
        for (int i = 0; i < successfulBids.size(); i++) {
            final SelectionsRequestBidDto bid = successfulBids.get(i);
            final SelectionsResponseAwardDto awardDto = new SelectionsResponseAwardDto(
                null,
                dateUtil.getNowUTC(),
                Status.PENDING,
                null,
                bid.getRelatedLots(),
                bid.getId(),
                bid.getValue(),
                bid.getTenderers(),
                null
            );
            awards.add(awardDto);
        }
        return awards;
    }

    private List<SelectionsResponseAwardDto> getUnsuccessfulAwards(final List<String> unSuccessfulLots) {
        final List<SelectionsResponseAwardDto> awards = new ArrayList<>();
        for (int i = 0; i < unSuccessfulLots.size(); i++) {
            final List<String> relatedLots = new ArrayList<>();
            relatedLots.add(unSuccessfulLots.get(i));
            final SelectionsResponseAwardDto awardDto = new SelectionsResponseAwardDto(
                generateAwardId(),
                dateUtil.getNowUTC(),
                Status.UNSUCCESSFUL,
                null,
                relatedLots,
                null,
                null,
                null,
                null);
            awards.add(awardDto);
        }
        return awards;
    }

    private void sortSuccessfulAwards(List<SelectionsResponseAwardDto> awards){
        Collections.sort(awards,new SortedByValue());
    }

    private void setStatusConsideration(List<SelectionsResponseAwardDto> awards){
        awards.get(0).setStatus(Status.CONSIDERATION);
    }

    private class SortedByValue implements Comparator<SelectionsResponseAwardDto> {

        public int compare(SelectionsResponseAwardDto obj1, SelectionsResponseAwardDto obj2) {

            double val1 = obj1.getValue().getAmount();
            double val2 = obj2.getValue().getAmount();

            if(val1 > val2) {
                return 1;
            }
            else if(val1 < val2) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

}
