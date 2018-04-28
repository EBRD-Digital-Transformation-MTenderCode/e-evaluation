package com.procurement.evaluation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.evaluation.model.dto.ocds.Award;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateAwardResponseDto {

    @JsonProperty("award")
    private final Award award;

    @JsonProperty("nextAward")
    private final Award nextAward;

    @JsonCreator
    public UpdateAwardResponseDto(@JsonProperty("award") final Award award,
                                  @JsonProperty("nextAward") final Award nextAward) {
        this.award = award;
        this.nextAward = nextAward;
    }
}
