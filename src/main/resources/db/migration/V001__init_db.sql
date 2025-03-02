CREATE TABLE event_store
(
    id         UUID    NOT NULL PRIMARY KEY,
    payload    JSONB   NOT NULL,
    metadata   JSONB   NOT NULL,
    stream_id  VARCHAR NOT NULL,
    version    INT     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT unique_stream_id_version UNIQUE (stream_id, version)
);

CREATE INDEX idx_stream_id ON event_store (stream_id);
