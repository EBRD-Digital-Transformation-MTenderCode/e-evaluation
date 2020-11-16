package com.procurement.evaluation.lib.functional

inline fun <T, E> validationRule(crossinline block: (value: T) -> Validated<E>) = ValidationRule.invoke(block)

interface ValidationRule<T, out E> {
    fun test(value: T): Validated<E>

    companion object {
        inline operator fun <T, E> invoke(crossinline block: (value: T) -> Validated<E>): ValidationRule<T, E> =
            object : ValidationRule<T, E> {
                override fun test(value: T) = block(value)
            }
    }
}
