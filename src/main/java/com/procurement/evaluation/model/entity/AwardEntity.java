package com.procurement.evaluation.model.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("evaluation_award")
public class AwardEntity {
    @PrimaryKeyColumn(name = "oc_id", type = PrimaryKeyType.PARTITIONED)
    private String ocId;

    @PrimaryKeyColumn(name = "award_id", type = PrimaryKeyType.CLUSTERED)
    private UUID awardId;

    @Column(value = "json_data")
    private String jsonData;

    @Column(value = "award_status_details")
    private String statusDetails;

    @Column(value = "award_status")
    private String status;

    @Column(value = "stage")
    private String stage;

    @Column(value = "owner")
    private String owner;
}
