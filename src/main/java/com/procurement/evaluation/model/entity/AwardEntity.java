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

    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private String cpId;

    @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
    private String stage;

    @PrimaryKeyColumn(value = "token_entity", type = PrimaryKeyType.CLUSTERED)
    private UUID token;

    @Column(value = "status")
    private String status;

    @Column(value = "status_details")
    private String statusDetails;

    @Column(value = "owner")
    private String owner;

    @Column(value = "json_data")
    private String jsonData;
}
