package com.procurement.evaluation.model.dto.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder("id")
public class Lot {

    @NotNull
    @JsonProperty("id")
    private final String id;

    @JsonCreator
    public Lot(@JsonProperty("id") final String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Lot)) {
            return false;
        }
        final Lot rhs = (Lot) other;
        return new EqualsBuilder().append(id, rhs.id)
                .isEquals();
    }
}
