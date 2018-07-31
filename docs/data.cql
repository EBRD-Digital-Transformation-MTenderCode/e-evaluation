CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.evaluation_rules (
    country text,
    pmd text,
    parameter text,
    value text,
    primary key(country, pmd, parameter)
);

INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'RT', 'minBids', '5');
INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'minBids', '3');
INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_RT', 'minBids', '3');

CREATE TABLE IF NOT EXISTS  ocds.evaluation_period (
    cp_id text,
    stage text,
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