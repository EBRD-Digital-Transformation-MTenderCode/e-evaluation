package com.procurement.evaluation.model.dto.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder("awards")
public class AwardRequestDto {

    @JsonProperty("awards")
    @NotNull
    @Valid
    private final AwardBidRQDto awards;
    private String cpId;
    private String token;
    private String owner;

    @JsonCreator
    public AwardRequestDto(
        @NotNull
        @Valid
        @JsonProperty("awards") final AwardBidRQDto awards) {

        this.awards = awards;
    }
}
