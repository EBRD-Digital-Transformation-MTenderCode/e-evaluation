package com.procurement.evaluation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.award.AwardBidRSDto;
import com.procurement.evaluation.model.dto.ocds.Award;
import com.procurement.evaluation.model.dto.ocds.Lot;
import com.procurement.evaluation.model.dto.ocds.Period;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AwardsResponseDto {

    private final List<Award> awards;

    private final Period period;

    private final List<Lot> unsuccessfulLots;

    @JsonCreator
    public AwardsResponseDto(@JsonProperty("awards") final List<Award> awards,
                             @JsonProperty("awardPeriod") final Period period,
                             @JsonProperty("unsuccessfulLots") final List<Lot> unsuccessfulLots) {
        this.awards = awards;
        this.period = period;
        this.unsuccessfulLots = unsuccessfulLots;
    }
}
