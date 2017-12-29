package com.procurement.evaluation.model.dto.selections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.evaluation.databinding.LocalDateTimeDeserializer;
import com.procurement.evaluation.databinding.LocalDateTimeSerializer;
import com.procurement.evaluation.model.dto.OrganizationReferenceDto;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "relatedLots",
    "date",
    "pendingDate",
    "createdDate",
    "tenderers"
})
public class SelectionsRequestBidDto {
    @JsonProperty("id")
    @JsonPropertyDescription("A local identifier for this bid")
    @NotNull
    private final String id;

    @JsonProperty("relatedLots")
    @JsonPropertyDescription("If this bid relates to one or more specific lots, provide the identifier(s) of the " +
        "related lot(s) here.")
    @NotEmpty
    @Valid
    private final List<String> relatedLots;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull
    private final LocalDateTime date;

    @JsonProperty("pendingDate")
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime pendingDate;

    @JsonProperty("createdDate")
    @NotNull
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime createdDate;

    @JsonProperty("tenderers")
    @JsonPropertyDescription("The party, or parties, responsible for this bid. This should provide a name and " +
        "identifier, cross-referenced to an entry in the parties array at the top level of the release.")
    @NotEmpty
    @Valid
    private final List<OrganizationReferenceDto> tenderers;

    @JsonCreator
    public SelectionsRequestBidDto(@JsonProperty("id") final String id,
                                   @JsonProperty("relatedLots") final List<String> relatedLots,
                                   @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                   @JsonProperty("date") final LocalDateTime date,
                                   @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                   @JsonProperty("pendingDate") final LocalDateTime pendingDate,
                                   @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                   @JsonProperty("createdDate") final LocalDateTime createdDate,
                                   @JsonProperty("tenderers") final List<OrganizationReferenceDto> tenderers
    ) {
        this.id = id;
        this.relatedLots = relatedLots;
        this.date = date;
        this.pendingDate = pendingDate;
        this.createdDate = createdDate;
        this.tenderers = tenderers;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(relatedLots)
                                    .append(date)
                                    .append(pendingDate)
                                    .append(createdDate)
                                    .append(tenderers)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SelectionsRequestBidDto)) {
            return false;
        }
        final SelectionsRequestBidDto rhs = (SelectionsRequestBidDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(relatedLots, rhs.relatedLots)
                                  .append(date, rhs.date)
                                  .append(pendingDate, rhs.pendingDate)
                                  .append(createdDate, rhs.createdDate)
                                  .append(tenderers, rhs.tenderers)
                                  .isEquals();
    }
}
