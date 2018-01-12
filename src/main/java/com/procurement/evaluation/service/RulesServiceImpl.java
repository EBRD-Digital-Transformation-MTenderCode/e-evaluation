package com.procurement.evaluation.service;
import com.procurement.evaluation.repository.RulesRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RulesServiceImpl implements RulesService {

    private final RulesRepository rulesRepository;

    public RulesServiceImpl(final RulesRepository rulesRepository) {
        this.rulesRepository = rulesRepository;
    }

    @Override
    public int getMinimumNumberOfBids(final String country, final String pocurementMethodDetails) {
        return getValue(country,
                        pocurementMethodDetails,
                        "minimumNumberOfBids")
            .map(Integer::valueOf)
            .orElse(0);
    }

    public Optional<String> getValue(final String country,
                                     final String pocurementMethodDetails,
                                     final String parameter) {
        final String value = rulesRepository.getValue(country, pocurementMethodDetails, parameter);
        return Optional.of(value);
    }
}
