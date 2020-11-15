package com.procurement.evaluation.infrastructure.api

interface Action {
    val key: String
    val kind: Kind

    enum class Kind { COMMAND, QUERY }
}
