package com.procurement.evaluation.infrastructure.exception

class CoefficientException(coefficient: String, description: String = "") :
    RuntimeException("Incorrect coefficient: '$coefficient'. $description")