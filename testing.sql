select lo_get(cast(payload as bigint))   as payload,
       lo_get(cast(meta_data as bigint)) as meta_data,
       *
from domain_event_entry
order by aggregate_identifier, time_stamp;

select lo_get(cast(payload as bigint))     as payload,
       lo_get(cast(meta_data as bigint))   as meta_data,
       lo_get(cast(diagnostics as bigint)) as diagnostics,
       lo_get(cast(token as bigint))       as token,
       *
from dead_letter_entry;

select *
from merchant_projection;
