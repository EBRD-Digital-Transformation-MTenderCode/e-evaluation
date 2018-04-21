package com.procurement.evaluation.model.dto.selections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.evaluation.model.dto.ocds.Bid;
import com.procurement.evaluation.model.dto.ocds.Lot;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SelectionsRequestDto {

    @Valid
    @NotNull
    @JsonProperty("lots")
    private final List<Lot> lots;

    @Valid
    @NotNull
    @JsonProperty("bids")
    private final List<Bid> bids;

    @JsonCreator
    public SelectionsRequestDto(@JsonProperty("lots") final List<Lot> lots,
                                @JsonProperty("bids") final List<Bid> bids) {
        this.lots = lots;
        this.bids = bids;
    }
}
