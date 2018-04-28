package com.procurement.evaluation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.evaluation.model.dto.ocds.Award;
import java.util.List;
import lombok.Getter;

@Getter
public class UpdateAwardResponseDto {

    @JsonProperty("award")
    private final Award award;

    @JsonProperty("nextAward")
    private final Award nextAward;

    @JsonProperty("bidId")
    private final String bidId;

    @JsonProperty("lotId")
    private final String lotId;

    @JsonCreator
    public UpdateAwardResponseDto(@JsonProperty("award") final Award award,
                                  @JsonProperty("nextAward") final Award nextAward,
                                  @JsonProperty("bidId") final String bidId,
                                  @JsonProperty("lotId") final String lotId) {
        this.award = award;
        this.nextAward = nextAward;
        this.bidId = bidId;
        this.lotId = lotId;
    }
}
