    -- // Query 6
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    -- //            the top <num> pairs of users who are not friends but have a lot of
    -- //            common friends
    -- //        (B) For each pair identified in (A), find the IDs, first names, and last names
    -- //            of all the two users' common friends

CREATE OR REPLACE VIEW MutualPairs AS
WITH FriendsOf AS (
    SELECT user1_id AS user_id, user2_id AS friend_id
    FROM project2.Public_Friends
    UNION ALL
    SELECT user2_id AS user_id, user1_id AS friend_id
    FROM project2.Public_Friends
),
PairMutuals AS (
    SELECT F1.user_id AS id1,
           F2.user_id AS id2,
           F1.friend_id AS mutual_id
    FROM FriendsOf F1
    JOIN FriendsOf F2
      ON F1.friend_id = F2.friend_id
     AND F1.user_id < F2.user_id
)
SELECT id1, id2, COUNT(DISTINCT mutual_id) AS num_mutuals
FROM PairMutuals
GROUP BY id1, id2;



SELECT M.id1,
       M.id2,
       M.num_mutuals,
       U1.first_name AS first_name1,
       U1.last_name AS last_name1,
       U2.first_name AS first_name2,
       U2.last_name AS last_name2
FROM MutualPairs M
JOIN project2.Public_Users U1
  ON U1.user_id = M.id1
JOIN project2.Public_Users U2
  ON U2.user_id = M.id2
ORDER BY M.num_mutuals DESC
FETCH FIRST 2 ROWS ONLY; -- ? in JDBC



WITH FriendsOf AS (
    SELECT user1_id AS user_id, user2_id AS friend_id
    FROM project2.Public_Friends
    UNION ALL
    SELECT user2_id AS user_id, user1_id AS friend_id
    FROM project2.Public_Friends
),
PairMutuals AS (
    SELECT F1.user_id AS id1,
           F2.user_id AS id2,
           F1.friend_id AS mutual_id
    FROM FriendsOf F1
    JOIN FriendsOf F2
      ON F1.friend_id = F2.friend_id
     AND F1.user_id < F2.user_id
)
SELECT U.user_id,
       U.first_name,
       U.last_name
FROM PairMutuals PM
JOIN project2.Public_Users U
  ON U.user_id = PM.mutual_id
WHERE PM.id1 = 420 -- id1 in JDBC
  AND PM.id2 = 69 -- id2 in JDBC
ORDER BY U.user_id ASC;

