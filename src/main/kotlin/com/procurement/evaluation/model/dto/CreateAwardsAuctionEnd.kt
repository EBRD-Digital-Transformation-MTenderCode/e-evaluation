package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.*

data class CreateAwardsAuctionEndRq @JsonCreator constructor(

        val tender: TenderAuctionEnd,

        val awardCriteria: String,

        val lots: List<Lot>,

        val bids: List<Bid>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAwardsAuctionEndRs @JsonCreator constructor(

        val awardPeriod: Period,

        val awards: List<Award>,

        val unsuccessfulLots: List<Lot>
)

data class TenderAuctionEnd @JsonCreator constructor(

        val electronicAuctions: ElectronicAuctions
)

data class ElectronicAuctions @JsonCreator constructor(

        val details: Set<ElectronicAuctionsDetails>
)

data class ElectronicAuctionsDetails @JsonCreator constructor(

        val relatedLot: String,

        val electronicAuctionResult: Set<ElectronicAuctionResult>
)

data class ElectronicAuctionResult @JsonCreator constructor(

        val relatedBid: String,

        val value: Value
)