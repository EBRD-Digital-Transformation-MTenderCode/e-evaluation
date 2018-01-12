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
    "rationale",
    "awards"
})
public class AwardRequestDto {
    @JsonProperty("ocid")
    @NotNull
    private final String ocid;
    @JsonProperty("awards")
    @NotNull
    @Valid
    private final AwardBidDto awards;
    @JsonProperty("rationale")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rationale;

    @JsonCreator
    public AwardRequestDto(
        @NotNull
        @JsonProperty("ocid") final String ocid,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("rationale") final String rationale,
        @NotNull
        @Valid
        @JsonProperty("awards") final AwardBidDto awards) {
        this.ocid = ocid;
        this.rationale = rationale;
        this.awards = awards;
    }
}
