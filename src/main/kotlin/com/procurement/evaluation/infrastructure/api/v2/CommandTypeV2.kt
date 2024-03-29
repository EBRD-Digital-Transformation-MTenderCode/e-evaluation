package com.procurement.evaluation.infrastructure.api.v2

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.infrastructure.api.Action

enum class CommandTypeV2(@JsonValue override val key: String, override val kind: Action.Kind) :
    Action,
    EnumElementProvider.Key {

    ADD_REQUIREMENT_RESPONSE(key = "addRequirementResponse", kind = Action.Kind.COMMAND),
    CHECK_ACCESS_TO_AWARD(key = "checkAccessToAward", kind = Action.Kind.QUERY),
    CHECK_AWARDS_STATE(key = "checkAwardsState", kind = Action.Kind.QUERY),
    CHECK_RELATED_TENDERER(key = "checkRelatedTenderer", kind = Action.Kind.QUERY),
    CLOSE_AWARD_PERIOD(key = "closeAwardPeriod", kind = Action.Kind.COMMAND),
    CREATE_AWARD(key = "createAward", kind = Action.Kind.COMMAND),
    CREATE_UNSUCCESSFUL_AWARDS(key = "createUnsuccessfulAwards", kind = Action.Kind.COMMAND),
    DO_CONSIDERATION(key = "doConsideration", kind = Action.Kind.COMMAND),
    GET_AWARD_STATES_BY_IDS(key = "getAwardStateByIds", kind = Action.Kind.QUERY),
    GET_AWARD_BY_IDS(key = "getAwardByIds", kind = Action.Kind.QUERY),
    FINALIZE_AWARDS(key = "finalizeAwards", kind = Action.Kind.COMMAND),
    FIND_AWARDS_FOR_PROTOCOL(key = "findAwardsForProtocol", kind = Action.Kind.QUERY),
    START_AWARD_PERIOD(key = "startAwardPeriod", kind = Action.Kind.COMMAND),
    UPDATE_AWARD(key = "updateAward", kind = Action.Kind.COMMAND),
    VALIDATE_AWARD_DATA(key = "validateAwardData", kind = Action.Kind.QUERY),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandTypeV2>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CommandTypeV2.orThrow(name)
    }
}
