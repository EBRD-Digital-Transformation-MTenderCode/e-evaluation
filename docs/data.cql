CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.evaluation_rules (
    country text,
    method text,
    parameter text,
    value text,
    primary key(country, method, parameter)
);

INSERT INTO ocds.evaluation_rules (country, method, parameter, value)  VALUES ('UA', 'micro', 'minimumNumberOfBids', '5');

CREATE TABLE IF NOT EXISTS  ocds.evaluation_period (
    oc_id text,
    start_date timestamp,
    end_date timestamp,
    primary key(oc_id)
);

CREATE TABLE IF NOT EXISTS  ocds.evaluation_award (
    oc_id text,
    owner text,
    award_id UUID,
    stage text,
    award_status text,
    award_status_details text,
    json_data text,
    primary key(oc_id, award_id)
);