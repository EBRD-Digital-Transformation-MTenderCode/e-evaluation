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

INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'OT', 'minBids', '1');
INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'SV', 'minBids', '1');
INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_OT', 'minBids', '1');
INSERT INTO ocds.evaluation_rules (country, pmd, parameter, value)  VALUES ('MD', 'TEST_SV', 'minBids', '1');


CREATE KEYSPACE IF NOT EXISTS evaluation WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS evaluation.history
(
    command_id   TEXT,
    command_name TEXT,
    command_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (command_id, command_name)
);

CREATE TABLE IF NOT EXISTS evaluation.rules
(
    country   TEXT,
    pmd       TEXT,
    parameter TEXT,
    value     TEXT,
    PRIMARY KEY (country, pmd, parameter)
);

CREATE TABLE IF NOT EXISTS evaluation.periods
(
    cpid           TEXT,
    ocid           TEXT,
    award_criteria TEXT,
    start_date     TIMESTAMP,
    end_date       TIMESTAMP,
    PRIMARY KEY (cpid, ocid)
);