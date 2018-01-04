CREATE KEYSPACE IF NOT EXISTS ocds WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

CREATE TABLE IF NOT EXISTS  ocds.storage_files (
    file_id UUID,
    file_is_open boolean,
    date_modified timestamp,
    date_published timestamp,
    file_hash text,
    file_weight bigint,
    file_name text,
    file_on_server text,
    file_owner text,
    primary key(file_id, file_is_open)

);

SELECT * FROM ocds.storage_files;

