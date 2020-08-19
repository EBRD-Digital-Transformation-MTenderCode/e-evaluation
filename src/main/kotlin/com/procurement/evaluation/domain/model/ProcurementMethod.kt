package com.procurement.evaluation.domain.model

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException

enum class ProcurementMethod(@JsonValue val value: String) {
    DA("limited"),
    FA("limited"),
    GPA("selective"),
    MV("open"),
    NP("limited"),
    OP("selective"),
    OT("open"),
    RT("selective"),
    SV("open"),
    TEST_DA("limited"),
    TEST_FA("limited"),
    TEST_GPA("selective"),
    TEST_MV("open"),
    TEST_NP("limited"),
    TEST_OP("selective"),
    TEST_OT("open"),
    TEST_RT("selective"),
    TEST_SV("open");

    override fun toString(): String {
        return this.value
    }

    companion object {
        fun fromString(name: String): ProcurementMethod = try {
            valueOf(name.toUpperCase())
        } catch (exception: Exception) {
            throw EnumException(
                enumType = ProcurementMethod::class.java.name,
                value = name,
                values = values().toString()
            )
        }
    }
}
