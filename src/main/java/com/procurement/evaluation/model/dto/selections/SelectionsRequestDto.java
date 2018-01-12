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
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "ocid",
    "country",
    "stage",
    "awardCriteria",
    "procurementMethodDetails",
    "lots",
    "bids"
})
public class SelectionsRequestDto {

    @JsonProperty("ocid")
    @NotNull
    private final String ocid;

    @JsonProperty("country")
    @JsonPropertyDescription("ISO Country Code of the country where the law applies. Use ISO Alpha-2 country codes.")
    @Size(min = 2, max = 3)
    @NotNull
    private final String country;

    @JsonProperty("stage")
    @NotNull
    private final String stage;

    @JsonProperty("awardCriteria")
    @NotNull
    private final String awardCriteria;

    @JsonProperty("procurementMethodDetails")
    @JsonPropertyDescription("Additional detail on the procurement method used. This field may be used to provide the" +
        " local name of the particular procurement method used.")
    @NotNull
    private final String procurementMethodDetails;

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

    @JsonCreator
    public SelectionsRequestDto(@JsonProperty("ocid") final String ocid,
                                @JsonProperty("country") final String country,
                                @JsonProperty("stage") final String stage,
                                @JsonProperty("awardCriteria") final String awardCriteria,
                                @JsonProperty("procurementMethodDetails") final String procurementMethodDetails,
                                @JsonProperty("lots") final List<LotDto> lots,
                                @JsonProperty("bids") final List<SelectionsRequestBidDto> bids) {
        this.ocid = ocid;
        this.country = country;
        this.stage = stage;
        this.awardCriteria = awardCriteria;
        this.procurementMethodDetails = procurementMethodDetails;
        this.lots = lots;
        this.bids = bids;
    }
}
