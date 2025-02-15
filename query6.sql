    -- // Query 6
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    -- //            the top <num> pairs of users who are not friends but have a lot of
    -- //            common friends
    -- //        (B) For each pair identified in (A), find the IDs, first names, and last names
    -- //            of all the two users' common friends

-- PerpetuaPanthers19!


CREATE VIEW MutualPairs AS
WITH FriendsOf AS (
    SELECT user1_id AS user_id, user2_id AS friend_id FROM project2.Public_Friends
    UNION ALL
    SELECT user2_id AS user_id, user1_id AS friend_id FROM project2.Public_Friends
), 
PairMutuals AS (
    SELECT
        F1.user_id       AS id1,
        F2.user_id       AS id2,
        F1.friend_id     AS mutual_id
    FROM FriendsOf F1
    JOIN FriendsOf F2
       ON F1.friend_id = F2.friend_id
      AND F1.user_id < F2.user_id
)
SELECT
    id1,
    id2,
    COUNT(DISTINCT mutual_id) AS num_mutuals
FROM PairMutuals
GROUP BY id1, id2
ORDER BY num_mutuals DESC, id1 ASC;


-- this one works
SELECT U1.user_id, U1.first_name, U1.last_name, U2.user_id, U2.first_name, U2.last_name, M.num_mutuals
FROM MutualPairs M
JOIN project2.Public_Users U1 ON U1.user_id = M.id1
JOIN project2.Public_Users U2 ON U2.user_id = M.id2
ORDER BY M.num_mutuals DESC
FETCH FIRST 5 ROWS ONLY;


-- part b is giving me troubles becaus eI don't know how to get the mutual's ID in their without it 
-- making the table all weird and giving a mutual count of 1. We cn brainstorm how to fix or whatnot
SELECT U.user_ID, U.first_name, U.last_name
FROM MutualPairs M
WHERE id1 = 42 AND id2 = 318 -- should be a ? in jdbc
JOIN project2.Public_Users U ON M.mutual_id = U.user_id;



DROP VIEW MutualPairs;