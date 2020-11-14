package com.procurement.evaluation.infrastructure.api.v2

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.infrastructure.api.Action

enum class CommandTypeV2(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    GET_AWARD_STATES_BY_IDS("getAwardStateByIds"),
    CHECK_ACCESS_TO_AWARD("checkAccessToAward"),
    CHECK_RELATED_TENDERER("checkRelatedTenderer"),
    ADD_REQUIREMENT_RESPONSE("addRequirementResponse"),
    CREATE_UNSUCCESSFUL_AWARDS("createUnsuccessfulAwards"),
    CLOSE_AWARD_PERIOD("closeAwardPeriod");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandTypeV2>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CommandTypeV2.orThrow(name)
    }
}