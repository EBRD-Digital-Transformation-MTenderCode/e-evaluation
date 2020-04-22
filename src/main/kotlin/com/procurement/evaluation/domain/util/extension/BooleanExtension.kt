package com.procurement.evaluation.domain.util.extension

inline fun Boolean.doOnFalse(block: () -> Nothing) {
    if (!this) block()
}