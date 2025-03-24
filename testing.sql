-- Events
select concat(type, '-', aggregate_identifier) as aggregate_id,
       payload_type                            as payload_type,
       lo_get(payload)                         as payload
--lo_get(meta_data)                       as meta_data,
--*
from domain_event_entry
order by aggregate_identifier, time_stamp;

-- DLQ
select concat(type, '-', aggregate_identifier) as aggregate_id,
       payload_type,
       lo_get(diagnostics)                     as diagnostics,
       lo_get(payload)                         as payload,
       cause_message,
       cause_type
-- lo_get(meta_data)    as meta_data,
-- *
from dead_letter_entry
order by enqueued_at;

select *
from merchant_projection;
