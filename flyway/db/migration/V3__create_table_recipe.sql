CREATE TABLE recipe
(
    id         uuid        not null,
    name       text        not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    primary key (id)
);

CREATE TABLE recipe_input
(
    recipe_id  uuid        not null references recipe(id),
    item_id    uuid        not null references item(id),
    count      bigint      not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    primary key (recipe_id, item_id)
);

CREATE TABLE recipe_output
(
    recipe_id  uuid        not null references recipe(id),
    item_id    uuid        not null references item(id),
    count      bigint      not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    primary key (recipe_id, item_id)
);