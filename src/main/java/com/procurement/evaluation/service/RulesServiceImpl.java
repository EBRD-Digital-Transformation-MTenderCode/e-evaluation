package com.procurement.evaluation.service;

import com.procurement.evaluation.exception.ErrorException;
import com.procurement.evaluation.exception.ErrorType;
import com.procurement.evaluation.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {

    private static final String PARAMETER_MIN_BIDS = "minBids";

    private final RulesRepository rulesRepository;

    public RulesServiceImpl(final RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public int getMinimumNumberOfBids(final String country, final String method) {
        return Optional.ofNullable(rulesRepository.getValue(country, method, PARAMETER_MIN_BIDS))
                .map(Integer::parseInt)
                .orElseThrow(() -> new ErrorException(ErrorType.BIDS_RULES_NOT_FOUND));
    }
}
