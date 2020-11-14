package com.procurement.evaluation.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.infrastructure.api.Action

enum class CommandTypeV1(@JsonValue override val key: String): Action {

    AWARDS_CANCELLATION("awardsCancellation"),
    CHECK_AWARD_STATUS("checkAwardStatus"),
    CREATE_AWARD("createAward"),
    CREATE_AWARDS("createAwards"),
    CREATE_AWARDS_AUCTION_END("createAwardsAuctionEnd"),
    CREATE_UNSUCCESSFUL_AWARDS("createUnsuccessfulAwards"),
    END_AWARD_PERIOD("endAwardPeriod"),
    EVALUATE_AWARD("evaluateAward"),
    FINAL_AWARDS_STATUS_BY_LOTS("finalAwardsStatusByLots"),
    GET_AWARD_ID_FOR_CHECK("getAwardIdForCheck"),
    GET_AWARDS_FOR_AC("getAwardsForAc"),
    GET_EVALUATED_AWARDS("getEvaluatedAwards"),
    GET_LOT_FOR_CHECK("getLotForCheck"),
    GET_NEXT_AWARD("getNextAward"),
    GET_UNSUCCESSFUL_LOTS("getUnsuccessfulLots"),
    GET_WINNING_AWARD("getWinAward"),
    SET_AWARD_FOR_EVALUATION("setAwardForEvaluation"),
    START_AWARD_PERIOD("startAwardPeriod"),
    START_CONSIDERATION("startConsideration"),
    ;

    override fun toString(): String {
        return this.key
    }
}
