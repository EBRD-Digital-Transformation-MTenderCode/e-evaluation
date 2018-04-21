package com.procurement.evaluation.model.dto.award;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.evaluation.model.dto.ocds.Document;
import com.procurement.evaluation.model.dto.ocds.Status;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "description",
    "statusDetails",
    "documents"
})
public class AwardBidRQDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("description")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String description;
    @JsonProperty("documents")
    @JsonPropertyDescription("All documents and attachments related to the award, including any notices.")
    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<Document> documents;
    @JsonProperty("statusDetails")
    @NotNull
    @Valid
    private Status statusDetails;

    @JsonCreator
    public AwardBidRQDto(@NotNull @JsonProperty("id") final String id,

                         @JsonProperty("description")
                         @JsonInclude(JsonInclude.Include.NON_NULL) final String description,
                         @NotNull
                         @Valid
                         @JsonProperty("statusDetails") final Status statusDetails,
                         @JsonInclude(JsonInclude.Include.NON_NULL)
                         @Valid
                         @JsonProperty("documents") final List<Document> documents
    ) {
        this.id = id;
        this.statusDetails = statusDetails;
        this.documents = documents;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(statusDetails)
                                    .append(documents)
                                    .append(description)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AwardBidRQDto)) {
            return false;
        }
        final AwardBidRQDto rhs = (AwardBidRQDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(documents, rhs.documents)
                                  .append(description, rhs.description)
                                  .isEquals();
    }
}
