package com.procurement.evaluation.infrastructure.dto.awards.create.auction.end.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateAwardsAuctionEndRequest
import org.junit.jupiter.api.Test

class CreateAwardsauctionEndRequestTest : AbstractDTOTestBase<CreateAwardsAuctionEndRequest>(
    CreateAwardsAuctionEndRequest::class.java
) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/auction/end/request/request_create_awards_auction_end_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/auction/end/request/request_create_awards_auction_end_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/auction/end/request/request_create_awards_auction_end_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/auction/end/request/request_create_awards_auction_end_required_3.json")
    }
}
