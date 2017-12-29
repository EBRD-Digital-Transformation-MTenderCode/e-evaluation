package com.procurement.evaluation.model.dto.endbid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.LotDto;
import com.procurement.evaluation.model.dto.award.AwardBidDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "ocid",
    "awards",
    "lots"
})
public class EndBidDto {
    @JsonProperty("ocid")
    private final String ocid;

    @JsonProperty("awards")
    private final List<EndBidAwardDto> awards;

    @JsonProperty("lots")
    private final List<LotDto> lots;


    @JsonCreator
    public EndBidDto(
        @NotNull
        @JsonProperty("ocid") final String ocid,
        @NotEmpty
        @Valid
        @JsonProperty("awards") final List<EndBidAwardDto> awards,
        @NotEmpty
        @JsonProperty("lots")
        final List<LotDto> lots) {
        this.ocid = ocid;
        this.awards = awards;
        this.lots=lots;
    }
}
