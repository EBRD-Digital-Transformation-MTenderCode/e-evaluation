package com.procurement.evaluation.model.dto.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.evaluation.databinding.LocalDateTimeDeserializer;
import com.procurement.evaluation.databinding.LocalDateTimeSerializer;
import com.procurement.evaluation.model.dto.DocumentDto;
import com.procurement.evaluation.model.dto.OrganizationReferenceDto;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    "suppliers",
    "documents"
})
public class AwardBidDto {
    @JsonProperty("id")
    @NotNull
    private String id;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull
    @Valid
    private final LocalDateTime startDate;

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

    @JsonProperty("relatedLots")
    @JsonPropertyDescription("If this award relates to one or more specific lots, provide the identifier(s) of the " +
        "related lot(s) here.")
    @NotEmpty
    private final List<String> relatedLots;
    @JsonProperty("relatedBid")
    @NotNull
    private final String relatedBid;
    @JsonProperty("suppliers")
    @JsonPropertyDescription("The suppliers awarded this award. If different suppliers have been awarded different " +
        "items of values, these should be split into separate award blocks.")
    @Valid
    @NotEmpty
    private final List<OrganizationReferenceDto> suppliers;
    @JsonProperty("documents")
    @JsonPropertyDescription("All documents and attachments related to the award, including any notices.")
    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<DocumentDto> documents;

    @JsonCreator
    public AwardBidDto(@NotNull @JsonProperty("id") final String id,
                       @JsonProperty("date") @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                       @NotNull
                       @Valid final LocalDateTime startDate,
                       @NotNull
                       @Valid
                       @JsonProperty("status") final Status status,
                       @NotNull
                       @Valid
                       @JsonProperty("statusDetails") final Status statusDetails,
                       @NotEmpty @JsonProperty("relatedLots") final List<String> relatedLots,
                       @NotNull
                       @JsonProperty("relatedBid") final String relatedBid,
                       @Valid
                       @NotEmpty
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
        this.suppliers = suppliers;
        this.documents = documents;
        this.relatedLots = relatedLots;
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
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AwardBidDto)) {
            return false;
        }
        final AwardBidDto rhs = (AwardBidDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(suppliers, rhs.suppliers)
                                  .append(documents, rhs.documents)
                                  .append(relatedLots, rhs.relatedLots)
                                  .append(relatedBid, rhs.relatedBid)
                                  .isEquals();
    }

    public enum Status {
        PENDING("pending"),
        ACTIVE("active"),
        UNSUCCESSFUL("unsuccessful");

        private static final Map<String, Status> CONSTANTS = new HashMap<>();

        static {
            for (final Status c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private final String value;

        Status(final String value) {
            this.value = value;
        }

        @JsonCreator
        public static Status fromValue(final String value) {
            final Status constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            }
            return constant;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

    }
}
