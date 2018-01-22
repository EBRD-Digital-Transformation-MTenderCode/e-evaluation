package com.procurement.evaluation.model.dto.selections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.LotDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "owner",
    "lots",
    "bids"
})
public class SelectionsRequestDto {

    @JsonProperty("owner")
    @NotNull
    private final String owner;
    @JsonProperty("lots")
    @JsonPropertyDescription("A tender process may be divided into lots, where bidders can bid on one or more lots. " +
        "Details of each lot can be provided here. Items, documents and other features can then reference the lot " +
        "they are related to using relatedLot. Where no relatedLot identifier is given, the values should be " +
        "interpreted as applicable to the whole tender. Properties of tender can be overridden for a given " +
        "SelectionsLotDto " +
        "through their inclusion in the SelectionsLotDto object.")
    @Valid
    @NotEmpty
    private final List<LotDto> lots;
    @JsonProperty("bids")
    @NotEmpty
    @Valid
    private final List<SelectionsRequestBidDto> bids;
    private String cpId;
    private String country;
    private String stage;
    private String awardCriteria;
    private String procurementMethodDetails;

    @JsonCreator
    public SelectionsRequestDto(@JsonProperty("owner") final String owner,
                                @JsonProperty("lots") final List<LotDto> lots,
                                @JsonProperty("bids") final List<SelectionsRequestBidDto> bids) {
        this.owner = owner;
        this.lots = lots;
        this.bids = bids;
    }
}
