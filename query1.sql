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
WHERE length(first_name) = (SELECT MAX(length(first_name))) 
AND first_name IS NOT NULL;


-- Part b
SELECT DISTINCT first_name
FROM project2.Public_Users
WHERE length(first_name) = (SELECT MIN(length(first_name))) 
AND first_name IS NOT NULL;

-- Part c
/*
SELECT first_name
FROM project2.Public_Users
WHERE first_name IS NOT NULL
GROUP BY first_name
*/


-- from chat for c
-- come back to this
SELECT first_name, COUNT(*) AS name_count
FROM project2.Public_Users
WHERE first_name IS NOT NULL
GROUP BY first_name
HAVING COUNT(*) = (
    -- find the maximum name_count
    SELECT MAX(name_count_sub)
    FROM (
        SELECT COUNT(*) AS name_count_sub
        FROM project2.Public_Users
        WHERE first_name IS NOT NULL
        GROUP BY first_name
    )sub
)
ORDER BY first_name ASC;

-- -- part d
-- SELECT first_name
-- FROM project2.Public_Users
-- GROUP BY first_name
-- HAVING COUNT(*) = num 
-- ORDER BY first_name ASC;