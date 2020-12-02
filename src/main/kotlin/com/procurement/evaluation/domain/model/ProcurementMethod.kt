package com.procurement.evaluation.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider

enum class ProcurementMethod(@JsonValue override val key: String) : EnumElementProvider.Key  {
    CD("selective"),
    CF("selective"),
    DA("limited"),
    DC("selective"),
    FA("limited"),
    GPA("selective"),
    IP("selective"),
    MV("open"),
    NP("limited"),
    OF("selective"),
    OP("selective"),
    OT("open"),
    RT("selective"),
    SV("open"),
    TEST_CD("selective"),
    TEST_CF("selective"),
    TEST_DA("limited"),
    TEST_DC("selective"),
    TEST_FA("limited"),
    TEST_GPA("selective"),
    TEST_IP("selective"),
    TEST_MV("open"),
    TEST_NP("limited"),
    TEST_OF("selective"),
    TEST_OP("selective"),
    TEST_OT("open"),
    TEST_RT("selective"),
    TEST_SV("open");

    override fun toString(): String {
        return this.key
    }

    companion object : EnumElementProvider<ProcurementMethod>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethod.orThrow(name)
    }
}
