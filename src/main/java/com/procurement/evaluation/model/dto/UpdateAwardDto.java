package com.procurement.evaluation.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.evaluation.model.dto.ocds.Award;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateAwardDto {

    @Valid
    @NotNull
    @JsonProperty("award")
    private final Award award;

    @JsonCreator
    public UpdateAwardDto(@JsonProperty("award") final Award award) {
        this.award = award;
    }
}
