package com.procurement.evaluation.model.dto.endbid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.ocds.Lot;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "awards",
    "lots"
})
public class EndBidDto {

    @JsonProperty("awards")
    private final List<EndBidAwardRSDto> awards;

    @JsonProperty("lots")
    private final List<Lot> lots;

    @JsonCreator
    public EndBidDto(
        @NotEmpty
        @Valid
        @JsonProperty("awards") final List<EndBidAwardRSDto> awards,
        @NotEmpty
        @JsonProperty("lots") final List<Lot> lots) {
        this.awards = awards;
        this.lots = lots;
    }
}
