package com.procurement.evaluation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.evaluation.model.dto.ocds.Award;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateAwardResponseDto {

    @JsonProperty("awards")
    private final List<Award> awards;

    @JsonProperty("bidId")
    private final String bidId;

    @JsonProperty("lotId")
    private final String lotId;

    @JsonCreator
    public UpdateAwardResponseDto(@JsonProperty("awards") final List<Award> awards,
                                  @JsonProperty("bidId") final String bidId,
                                  @JsonProperty("lotId") final String lotId) {
        this.awards = awards;
        this.bidId = bidId;
        this.lotId = lotId;
    }
}
