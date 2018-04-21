package com.procurement.evaluation.model.dto.bpe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

    @JsonProperty("success")
    private final Boolean success;

    @JsonProperty("details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<ResponseDetailsDto> details;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public ResponseDto(@JsonProperty("success") final Boolean success,
                       @JsonProperty("details") final List<ResponseDetailsDto> details,
                       @JsonProperty(value = "data") final T data) {
        this.success = success;
        this.details = details;
        this.data = data;
    }

}
