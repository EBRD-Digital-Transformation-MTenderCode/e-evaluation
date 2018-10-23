package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Value

data class CreateAwardsAuctionEndRq @JsonCreator constructor(

        val tender: TenderAuctionEnd,

        val bids: List<Bid>
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