package com.procurement.evaluation.model.dto.selections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.evaluation.databinding.LocalDateTimeDeserializer;
import com.procurement.evaluation.databinding.LocalDateTimeSerializer;
import com.procurement.evaluation.model.dto.DocumentDto;
import com.procurement.evaluation.model.dto.OrganizationReferenceDto;
import com.procurement.evaluation.model.dto.Status;
import com.procurement.evaluation.model.dto.Value;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@Setter
@JsonPropertyOrder({
    "id",
    "date",
    "status",
    "statusDetails",
    "relatedLots",
    "relatedBid",
    "value",
    "suppliers",
    "documents"
})
public class SelectionsResponseAwardDto {

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime startDate;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("statusDetails")
    private final Status statusDetails;
    @JsonProperty("relatedLots")
    private final List<String> relatedLots;
    @JsonProperty("relatedBid")
    private final String relatedBid;
    @JsonProperty("value")
    private final Value value;
    @JsonProperty("suppliers")
    private final List<OrganizationReferenceDto> suppliers;
    @JsonProperty("documents")
    private final List<DocumentDto> documents;
    @JsonProperty("id")
    private String id;

    @JsonCreator
    public SelectionsResponseAwardDto(
        @NotNull
        @JsonProperty("id") final String id,
        @NotNull
        @JsonProperty("date") @JsonDeserialize(using = LocalDateTimeDeserializer.class) final LocalDateTime startDate,
        @Valid
        @NotNull
        @JsonProperty("status") final Status status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Valid
        @JsonProperty("statusDetails") final Status statusDetails,
        @NotEmpty
        @JsonProperty("relatedLots") final List<String> relatedLots,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("relatedBid") final String relatedBid,
        @JsonProperty("value")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Valid
        final Value value,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Valid
        @JsonProperty("suppliers") final List<OrganizationReferenceDto> suppliers,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Valid
        @JsonProperty("documents") final List<DocumentDto> documents
    ) {
        this.id = id;
        this.startDate = startDate;
        this.status = status;
        this.statusDetails = statusDetails;
        this.relatedBid = relatedBid;
        this.value=value;
        this.suppliers = suppliers;
        this.documents = documents;
        this.relatedLots = relatedLots;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(status)
                                    .append(suppliers)
                                    .append(documents)
                                    .append(relatedLots)
                                    .append(relatedBid)
                                    .append(value)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SelectionsResponseAwardDto)) {
            return false;
        }
        final SelectionsResponseAwardDto rhs = (SelectionsResponseAwardDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(status, rhs.status)
                                  .append(suppliers, rhs.suppliers)
                                  .append(documents, rhs.documents)
                                  .append(relatedLots, rhs.relatedLots)
                                  .append(relatedBid, rhs.relatedBid)
                                  .append(value,rhs.value)
                                  .isEquals();
    }


}
