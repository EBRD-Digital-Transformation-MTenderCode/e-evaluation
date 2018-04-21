package com.procurement.evaluation.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("evaluation_period")
@Getter
@Setter
public class PeriodEntity {
    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private String cpId;

    @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
    private String stage;

    @Column(value = "start_date")
    private Date startDate;

    @Column(value = "end_date")
    private Date endDate;

    public LocalDateTime getStartDate() {
        return LocalDateTime.ofInstant(startDate.toInstant(), ZoneOffset.UTC);
    }

    public void setStartDate(final LocalDateTime startDate) {
        this.startDate = Date.from(startDate.toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.ofInstant(endDate.toInstant(), ZoneOffset.UTC);
    }

    public void setEndDate(final LocalDateTime endDate) {
        this.endDate = Date.from(endDate.toInstant(ZoneOffset.UTC));
    }
}
