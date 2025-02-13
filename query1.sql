/*
 Query 1
     -----------------------------------------------------------------------------------
     GOALS: (A) The first name(s) with the most letters
            (B) The first name(s) with the fewest letters
            (C) The first name held by the most users
            (D) The number of users whose first name is that identified in (C)
    */

-- Part a
SELECT DISTINCT first_name
FROM project2.Public_Users
WHERE length(first_name) = (
    SELECT MAX(length(first_name))
    FROM project2.Public_Users     -- added this from because the select wasn't selected anything
)
AND first_name IS NOT NULL;


-- Part b
SELECT DISTINCT first_name
FROM project2.Public_Users
WHERE length(first_name) = (
    SELECT MIN(length(first_name))
    FROM project2.Public_Users     -- same logic as (a)
)
AND first_name IS NOT NULL;

-- Part c
-- going to copy the logic of 0a and use FETCH FIRST 1 ROW ONLY
SELECT COUNT(*) AS mostName, First_Name
FROM project2.Public_Users
WHERE First_Name IS NOT NULL 
GROUP BY First_Name
ORDER BY mostName DESC
FETCH FIRST 1 ROW ONLY;



-- from chat for c
-- come back to this
-- SELECT first_name, COUNT(*) AS name_count
-- FROM project2.Public_Users
-- WHERE first_name IS NOT NULL
-- GROUP BY first_name
-- HAVING COUNT(*) = (
--     -- find the maximum name_count
--     SELECT MAX(name_count_sub)
--     FROM (
--         SELECT COUNT(*) AS name_count_sub
--         FROM project2.Public_Users
--         WHERE first_name IS NOT NULL
--         GROUP BY first_name
--     )sub
-- )
-- ORDER BY first_name ASC;



-- -- part d
-- SELECT first_name
-- FROM project2.Public_Users
-- GROUP BY first_name
-- HAVING COUNT(*) = num 
-- ORDER BY first_name ASC;