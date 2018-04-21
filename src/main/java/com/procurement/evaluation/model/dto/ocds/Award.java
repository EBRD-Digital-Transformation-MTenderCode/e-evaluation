package com.procurement.evaluation.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.evaluation.databinding.LocalDateTimeDeserializer;
import com.procurement.evaluation.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@Setter
@JsonPropertyOrder({
        "id",
        "token",
        "date",
        "description",
        "status",
        "statusDetails",
        "value",
        "relatedLots",
        "relatedBid",
        "suppliers",
        "documents"
})
public class Award {

    @NotNull
    @JsonProperty("id")
    private String id;

    @JsonProperty("token")
    private String token;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    @JsonProperty("description")
    private String description;

    @Valid
    @JsonProperty("status")
    private Status status;

    @Valid
    @NotNull
    @JsonProperty("statusDetails")
    private Status statusDetails;

    @JsonProperty("value")
    private final Value value;

    @JsonProperty("relatedLots")
    private List<String> relatedLots;

    @JsonProperty("relatedBid")
    private String relatedBid;

    @Valid
    @JsonProperty("suppliers")
    private List<OrganizationReference> suppliers;

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonCreator
    public Award(@JsonProperty("id") final String id,
                 @JsonProperty("token") final String token,
                 @JsonProperty("date") final LocalDateTime date,
                 @JsonProperty("description") final String description,
                 @JsonProperty("status") final Status status,
                 @JsonProperty("statusDetails") final Status statusDetails,
                 @JsonProperty("value") final Value value,
                 @JsonProperty("relatedLots") final List<String> relatedLots,
                 @JsonProperty("relatedBid") final String relatedBid,
                 @JsonProperty("suppliers") final List<OrganizationReference> suppliers,
                 @JsonProperty("documents") final List<Document> documents) {
        this.id = id;
        this.date = date;
        this.token = token;
        this.description = description;
        this.status = status;
        this.statusDetails = statusDetails != null ? statusDetails : Status.EMPTY;
        this.value = value;
        this.relatedBid = relatedBid;
        this.suppliers = suppliers;
        this.documents = documents;
        this.relatedLots = relatedLots;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(date)
                .append(status)
                .append(statusDetails)
                .append(value)
                .append(suppliers)
                .append(documents)
                .append(relatedLots)
                .append(relatedBid)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Award)) {
            return false;
        }
        final Award rhs = (Award) other;
        return new EqualsBuilder().append(id, rhs.id)
                .append(date, rhs.date)
                .append(status, rhs.status)
                .append(statusDetails, rhs.statusDetails)
                .append(value, rhs.value)
                .append(suppliers, rhs.suppliers)
                .append(documents, rhs.documents)
                .append(relatedLots, rhs.relatedLots)
                .append(relatedBid, rhs.relatedBid)
                .isEquals();
    }
}
