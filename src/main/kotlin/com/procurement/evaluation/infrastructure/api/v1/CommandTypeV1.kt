package com.procurement.evaluation.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.infrastructure.api.Action

enum class CommandTypeV1(@JsonValue override val key: String, override val kind: Action.Kind) : Action {

    AWARDS_CANCELLATION(key = "awardsCancellation", kind = Action.Kind.COMMAND),
    CHECK_AWARD_STATUS(key = "checkAwardStatus", kind = Action.Kind.QUERY),
    CREATE_AWARD(key = "createAward", kind = Action.Kind.COMMAND),
    CREATE_AWARDS(key = "createAwards", kind = Action.Kind.COMMAND),
    CREATE_AWARDS_AUCTION_END(key = "createAwardsAuctionEnd", kind = Action.Kind.COMMAND),
    CREATE_UNSUCCESSFUL_AWARDS(key = "createUnsuccessfulAwards", kind = Action.Kind.COMMAND),
    END_AWARD_PERIOD(key = "endAwardPeriod", kind = Action.Kind.COMMAND),
    EVALUATE_AWARD(key = "evaluateAward", kind = Action.Kind.COMMAND),
    FINAL_AWARDS_STATUS_BY_LOTS(key = "finalAwardsStatusByLots", kind = Action.Kind.COMMAND),
    GET_AWARD_ID_FOR_CHECK(key = "getAwardIdForCheck", kind = Action.Kind.QUERY),
    GET_AWARDS_FOR_AC(key = "getAwardsForAc", kind = Action.Kind.QUERY),
    GET_EVALUATED_AWARDS(key = "getEvaluatedAwards", kind = Action.Kind.QUERY),
    GET_LOT_FOR_CHECK(key = "getLotForCheck", kind = Action.Kind.QUERY),
    GET_NEXT_AWARD(key = "getNextAward", kind = Action.Kind.QUERY),
    GET_UNSUCCESSFUL_LOTS(key = "getUnsuccessfulLots", kind = Action.Kind.QUERY),
    GET_WINNING_AWARD(key = "getWinAward", kind = Action.Kind.QUERY),
    SET_AWARD_FOR_EVALUATION(key = "setAwardForEvaluation", kind = Action.Kind.COMMAND),
    START_AWARD_PERIOD(key = "startAwardPeriod", kind = Action.Kind.COMMAND),
    START_CONSIDERATION(key = "startConsideration", kind = Action.Kind.COMMAND),
    ;

    override fun toString(): String {
        return this.key
    }
}
