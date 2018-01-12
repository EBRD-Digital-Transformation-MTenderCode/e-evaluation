package com.procurement.evaluation.model.dto.bpe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class ResponseDto<T> {

    @JsonProperty("success")
    private final Boolean success;

    @JsonProperty("responseDetail")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<ResponseDetailsDto> responseDetail;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public ResponseDto(@JsonProperty("success") final Boolean success,
                       @JsonProperty("responseDetail") final List<ResponseDetailsDto> responseDetail,
                       @JsonProperty(value = "data") final T data) {
        this.success = success;
        this.responseDetail = responseDetail;
        this.data = data;
    }

    @Getter
    public static class ResponseDetailsDto {
        @JsonProperty("code")
        private final String code;
        @JsonProperty("message")
        private final String message;

        public ResponseDetailsDto(@JsonProperty("code") final String code,
                                  @JsonProperty("message") final String message) {
            this.code = code;
            this.message = message;
        }
    }
}
