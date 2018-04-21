package com.procurement.evaluation.model.dto.selections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.AwardPeriodDto;
import com.procurement.evaluation.model.dto.ocds.Lot;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "rationale",
    "awardPeriod",
    "awards",
    "lots"
})
public class SelectionsResponseDto {

    @JsonProperty("rationale")
    private final String rationale;

    @JsonProperty("awardPeriod")
    private final AwardPeriodDto period;

    @JsonProperty("awards")
    private final List<SelectionsResponseAwardDto> awards;

    @JsonProperty("lots")
    private final List<Lot> lots;

    @JsonCreator
    public SelectionsResponseDto(@JsonInclude(JsonInclude.Include.NON_NULL)
                                 @JsonProperty("rationale") final String rationale,
                                 @NotNull
                                 @Valid
                                 @JsonProperty("awardPeriod") final AwardPeriodDto period,
                                 @NotEmpty
                                 @Valid
                                 @JsonProperty("awards") final List<SelectionsResponseAwardDto> awards,
                                 @NotEmpty
                                 @JsonProperty("lots") final List<Lot> lots) {

        this.rationale = rationale;
        this.period = period;
        this.awards = awards;
        this.lots = lots;
    }
}
