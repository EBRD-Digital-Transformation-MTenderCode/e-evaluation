package com.procurement.evaluation.service;

import org.springframework.stereotype.Service;

@Service
public interface RulesService {

    int getMinimumNumberOfBids(String country, String pocurementMethodDetails);
}
