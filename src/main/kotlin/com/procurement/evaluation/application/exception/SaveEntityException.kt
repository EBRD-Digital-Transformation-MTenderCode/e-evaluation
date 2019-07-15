package com.procurement.evaluation.application.exception

class SaveEntityException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(message: String) : super(message)
}
