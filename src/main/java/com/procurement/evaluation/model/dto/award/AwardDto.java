package com.procurement.evaluation.model.dto.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "ocid",
    "awardPeriod",
    "rationale",
    "awards"
})
public class AwardDto {
    @JsonProperty("ocid")
    @NotNull
    private final String ocid;
    @JsonProperty("awardPeriod")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final AwardPeriodDto awardPeriod;

    @JsonProperty("awards")
    @NotNull
    @Valid
    private final List<AwardBidDto> awards;
    @JsonProperty("rationale")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rationale;

    @JsonCreator
    public AwardDto(
        @NotNull
        @JsonProperty("ocid") final String ocid,
        @NotNull
        @Valid
        @JsonProperty("awardPeriod") final AwardPeriodDto awardPeriod,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("rationale") final String rationale,
        @NotNull
        @Valid
        @JsonProperty("awards") final List<AwardBidDto> awards) {
        this.ocid = ocid;
        this.awardPeriod = awardPeriod;
        this.rationale = rationale;
        this.awards = awards;
    }
}
