    -- // Query 8 - Completed
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    -- //            with User ID <userID>
    -- //        (B) Find the ID, first name, and last name of the youngest friend of the user
    -- //            with User ID <userID>

CREATE VIEW FindAges AS
SELECT U.user_id, U.first_name, U.last_name, U.year_of_birth, U.month_of_birth, U.day_of_birth
FROM project2.Public_Users U
JOIN project2.Public_FRIENDS F 
    ON (F.user1_id = U.user_id OR F.user2_id = U.user_id)
WHERE 68 IN (F.user1_id, F.user2_id); -- replace with ?

-- youngest
SELECT user_id, first_name, last_name
FROM FindAges
ORDER BY year_of_birth ASC, month_of_birth ASC, day_of_birth ASC
FETCH FIRST 1 ROW ONLY;

-- oldest
SELECT user_id, first_name, last_name
FROM FindAges
ORDER BY year_of_birth DESC, month_of_birth DESC, day_of_birth DESC
FETCH FIRST 1 ROW ONLY;

DROP VIEW FindAges;