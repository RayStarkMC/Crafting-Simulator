alter table recipe_input
    drop constraint recipe_input_item_id_fkey;

alter table recipe_output
    drop constraint recipe_output_item_id_fkey;