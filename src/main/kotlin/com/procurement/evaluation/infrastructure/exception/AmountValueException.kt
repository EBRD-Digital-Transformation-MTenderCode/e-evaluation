package com.procurement.evaluation.infrastructure.exception

class AmountValueException(amount: String, description: String = "") :
    RuntimeException("Incorrect value of the amount: '$amount'. $description")
