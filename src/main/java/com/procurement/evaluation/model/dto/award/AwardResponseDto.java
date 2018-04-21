package com.procurement.evaluation.model.dto.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.ocds.Period;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({

    "awardPeriod",
    "awards"
})
public class AwardResponseDto {

    @JsonProperty("awardPeriod")
    private Period awardPeriod;
    @JsonProperty("awards")
    private List<AwardBidRSDto> awards;

    @JsonCreator
    public AwardResponseDto(
        @NotNull
        @Valid
        @JsonProperty("awardPeriod") final Period awardPeriod,
        @NotNull
        @Valid
        @JsonProperty("awards") final List<AwardBidRSDto> awards) {

        this.awardPeriod = awardPeriod;
        this.awards = awards;
    }
}
