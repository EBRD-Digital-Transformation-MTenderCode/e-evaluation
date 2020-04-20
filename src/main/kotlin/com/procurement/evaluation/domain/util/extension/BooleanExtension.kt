package com.procurement.evaluation.domain.util.extension

inline fun Boolean.isFalse(block: () -> Nothing) {
    if (!this) block()
}