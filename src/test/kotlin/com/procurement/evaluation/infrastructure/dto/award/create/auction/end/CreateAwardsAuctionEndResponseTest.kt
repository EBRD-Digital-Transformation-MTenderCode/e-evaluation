package com.procurement.evaluation.infrastructure.dto.award.create.auction.end

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CreateAwardsAuctionEndResponse
import org.junit.jupiter.api.Test

class CreateAwardsAuctionEndResponseTest :
    AbstractDTOTestBase<CreateAwardsAuctionEndResponse>(CreateAwardsAuctionEndResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/create/auction/end/response_create_awards_auction_end_full.json")
    }
}
