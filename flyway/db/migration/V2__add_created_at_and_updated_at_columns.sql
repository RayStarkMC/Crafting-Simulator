ALTER TABLE item
    ADD COLUMN created_at timestamptz not null default current_timestamp,
    ADD COLUMN updated_at timestamptz not null default current_timestamp;

ALTER TABLE item
    ALTER COLUMN created_at drop default,
    ALTER COLUMN updated_at drop default;