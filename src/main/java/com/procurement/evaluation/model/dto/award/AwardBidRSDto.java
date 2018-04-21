package com.procurement.evaluation.model.dto.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.evaluation.databinding.LocalDateTimeDeserializer;
import com.procurement.evaluation.databinding.LocalDateTimeSerializer;
import com.procurement.evaluation.model.dto.ocds.Document;
import com.procurement.evaluation.model.dto.ocds.OrganizationReference;
import com.procurement.evaluation.model.dto.ocds.Status;
import com.procurement.evaluation.model.dto.ocds.Value;
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
    "description",
    "status",
    "statusDetails",
    "relatedLots",
    "relatedBid",
    "value",
    "suppliers",
    "documents"
})
public class AwardBidRSDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull
    @Valid
    private final LocalDateTime startDate;
    @JsonProperty("description")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String description;
    @JsonProperty("relatedLots")
    @JsonPropertyDescription("If this award relates to one or more specific lots, provide the identifier(s) of the " +
        "related lot(s) here.")
    @NotEmpty
    private final List<String> relatedLots;
    @JsonProperty("relatedBid")
    @NotNull
    private final String relatedBid;
    @JsonProperty("value")
    @NotNull
    @Valid
    private final Value value;
    @JsonProperty("suppliers")
    @JsonPropertyDescription("The suppliers awarded this award. If different suppliers have been awarded different " +
        "items of values, these should be split into separate award blocks.")
    @Valid
    @NotEmpty
    private final List<OrganizationReference> suppliers;
    @JsonProperty("documents")
    @JsonPropertyDescription("All documents and attachments related to the award, including any notices.")
    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<Document> documents;
    @JsonProperty("status")
    @JsonPropertyDescription("The current status of the award drawn from the [awardStatus codelist](http://standard" +
        ".open-contracting.org/latest/en/schema/codelists/#award-status)")
    @NotNull
    @Valid
    private Status status;
    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private Status statusDetails;

    @JsonCreator
    public AwardBidRSDto(@NotNull @JsonProperty("id") final String id,
                         @JsonProperty("date") @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                         @NotNull
                         @Valid final LocalDateTime startDate,
                         @JsonInclude(JsonInclude.Include.NON_NULL)
                         @JsonProperty("description") final String description,
                         @NotNull
                         @Valid
                         @JsonProperty("status") final Status status,
                         @NotNull
                         @Valid
                         @JsonProperty("statusDetails") final Status statusDetails,
                         @NotEmpty @JsonProperty("relatedLots") final List<String> relatedLots,
                         @NotNull
                         @JsonProperty("relatedBid") final String relatedBid,
                         @JsonProperty("value")
                         @NotNull
                         @Valid final Value value,
                         @Valid
                         @NotEmpty
                         @JsonProperty("suppliers") final List<OrganizationReference> suppliers,
                         @JsonInclude(JsonInclude.Include.NON_NULL)
                         @Valid
                         @JsonProperty("documents") final List<Document> documents
    ) {
        this.id = id;
        this.startDate = startDate;
        this.status = status;
        this.statusDetails = statusDetails;
        this.relatedBid = relatedBid;
        this.value = value;
        this.suppliers = suppliers;
        this.documents = documents;
        this.relatedLots = relatedLots;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(status)
                                    .append(statusDetails)
                                    .append(suppliers)
                                    .append(documents)
                                    .append(relatedLots)
                                    .append(relatedBid)
                                    .append(value)
                                    .append(description)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AwardBidRSDto)) {
            return false;
        }
        final AwardBidRSDto rhs = (AwardBidRSDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(suppliers, rhs.suppliers)
                                  .append(documents, rhs.documents)
                                  .append(relatedLots, rhs.relatedLots)
                                  .append(relatedBid, rhs.relatedBid)
                                  .append(value, rhs.value)
                                  .append(description, rhs.description)
                                  .isEquals();
    }
}
