CREATE TABLE event_store
(
    id        UUID    NOT NULL PRIMARY KEY,
    stream_id VARCHAR NOT NULL,
    payload   JSONB   NOT NULL,
    metadata  JSONB   NOT NULL,
    version   INT     NOT NULL,
    created   DATE    NOT NULL DEFAULT CURRENT_DATE
);
