create or replace PROCEDURE drop_ifexists
( pv_type   VARCHAR2
, pv_table  VARCHAR2 )  IS
 
  -- String for DDL command.
  sql_text  VARCHAR2(2000);
 
  -- Declare a parameterized cursor.
  CURSOR find_object
  ( cv_type   VARCHAR2
  , cv_table  VARCHAR2 ) IS
    SELECT   uo.object_name
    ,        uo.object_type
    FROM     user_objects uo
    WHERE    uo.object_name = UPPER(cv_table)
    AND      uo.object_type = UPPER(cv_type);
 
BEGIN
 
  -- Open the cursor with the input variables.
  FOR i IN find_object(pv_type, pv_table) LOOP
 
    -- Check for a table object and append cascade constraints.
    IF i.object_type = 'TABLE' THEN
      sql_text := 'DROP '||i.object_type||' '||i.object_name||' CASCADE CONSTRAINTS';
    ELSE
      sql_text := 'DROP '||i.object_type||' '||i.object_name;
    END IF;
 
    -- Run dynamic command.
    EXECUTE IMMEDIATE sql_text;
 
  END LOOP;
 
END drop_ifexists; 
 