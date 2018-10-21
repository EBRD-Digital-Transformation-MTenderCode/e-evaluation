package com.procurement.evaluation.model.dto.selections

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.*
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class CreateAwardsAuctionRq @JsonCreator constructor(

        val tender: AuctionTender,

        val bidsData: Set<BidsData>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAwardsAuctionRs @JsonCreator constructor(

        val awards: List<Award>,

        val unsuccessfulLots: List<Lot>
)

data class AuctionTender @JsonCreator constructor(

        val id: String?,

        val title: String?,

        val description: String?,

        val awardCriteria: String,

        val lots: List<Lot>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsData @JsonCreator constructor(

        var owner: String?,

        var bids: Set<Bid>
)
