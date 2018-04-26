package com.procurement.evaluation.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.evaluation.databinding.LocalDateTimeDeserializer;
import com.procurement.evaluation.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "relatedLots",
        "date",
        "pendingDate",
        "createdDate",
        "value",
        "tenderers"
})
public class Bid {

    @NotNull
    @JsonProperty("id")
    private final String id;

    @Valid
    @NotEmpty
    @JsonProperty("relatedLots")
    private final List<String> relatedLots;


    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("date")
    private final LocalDateTime date;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("pendingDate")
    private final LocalDateTime pendingDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("createdDate")
    private final LocalDateTime createdDate;

    @Valid
    @NotNull
    private final Value value;

    @Valid
    @NotEmpty
    @JsonProperty("tenderers")
    private final List<OrganizationReference> tenderers;

    @JsonCreator
    public Bid(@JsonProperty("id") final String id,
               @JsonProperty("relatedLots") final List<String> relatedLots,
               @JsonProperty("date") final LocalDateTime date,
               @JsonProperty("pendingDate") final LocalDateTime pendingDate,
               @JsonProperty("createdDate") final LocalDateTime createdDate,
               @JsonProperty("value") final Value value,
               @JsonProperty("tenderers") final List<OrganizationReference> tenderers) {
        this.id = id;
        this.relatedLots = relatedLots;
        this.date = date;
        this.pendingDate = pendingDate;
        this.createdDate = createdDate;
        this.value = value;
        this.tenderers = tenderers;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                .append(relatedLots)
                .append(date)
                .append(pendingDate)
                .append(createdDate)
                .append(value)
                .append(tenderers)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Bid)) {
            return false;
        }
        final Bid rhs = (Bid) other;
        return new EqualsBuilder().append(id, rhs.id)
                .append(relatedLots, rhs.relatedLots)
                .append(date, rhs.date)
                .append(pendingDate, rhs.pendingDate)
                .append(createdDate, rhs.createdDate)
                .append(value, rhs.value)
                .append(tenderers, rhs.tenderers)
                .isEquals();
    }
}
