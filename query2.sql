    -- Query 2 - Completed
    -- -----------------------------------------------------------------------------------
    -- GOALS: (A) Find the IDs, first names, and last names of users without any friends
    -- 
    -- Be careful! Remember that if two users are friends, the Friends table only contains
    -- the one entry (U1, U2) where U1 < U2.

SELECT U.user_id, U.first_name, U.last_name
FROM project2.Public_Users U
WHERE NOT EXISTS (
    SELECT 1 
    FROM project2.Public_Friends F
    WHERE U.user_id = F.user1_id OR U.user_id = F.user2_id
);