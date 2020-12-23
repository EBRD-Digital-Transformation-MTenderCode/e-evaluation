package com.procurement.evaluation.exception


class EmptyStringException(val attributeName: String) : RuntimeException(attributeName)