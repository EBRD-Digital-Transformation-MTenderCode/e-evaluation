package com.procurement.evaluation.model.dto.selections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.ocds.Award;
import com.procurement.evaluation.model.dto.ocds.Lot;
import com.procurement.evaluation.model.dto.ocds.Period;
import java.util.List;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "awardPeriod",
        "awards",
        "lots"
})
public class SelectionsResponseDto {

    @JsonProperty("awardPeriod")
    private final Period period;

    @JsonProperty("awards")
    private final List<Award> awards;

    @JsonProperty("unsuccessfulLots")
    private final List<Lot> lots;

    @JsonCreator
    public SelectionsResponseDto(@JsonProperty("awardPeriod") final Period period,
                                 @JsonProperty("awards") final List<Award> awards,
                                 @JsonProperty("unsuccessfulLots") final List<Lot> lots) {

        this.period = period;
        this.awards = awards;
        this.lots = lots;
    }
}
