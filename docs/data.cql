CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.evaluation_rules (
    country text,
    pmd text,
    parameter text,
    value text,
    primary key(country, pmd, parameter)
);

CREATE TABLE IF NOT EXISTS  ocds.evaluation_period (
    cp_id text,
    stage text,
    award_criteria text,
    start_date timestamp,
    end_date timestamp,
    primary key(cp_id, stage)
);

CREATE TABLE IF NOT EXISTS  ocds.evaluation_award (
    cp_id text,
    stage text,
    token_entity uuid,
    status text,
    status_details text,
    owner text,
    json_data text,
    primary key(cp_id, stage, token_entity)
);

CREATE TABLE IF NOT EXISTS  ocds.evaluation_history (
    operation_id text,
    command text,
    operation_date timestamp,
    json_data text,
    primary key(operation_id, command)
);

INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'minBids', '2');
INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'minBids', '2');
