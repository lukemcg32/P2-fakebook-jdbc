    -- // Query 5
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    -- //            users in the top <num> pairs of users that meet each of the following
    -- //            criteria:
    -- //             x (i) same gender 
    -- //             x (ii) tagged in at least one common photo
    -- //             x (iii) difference in birth years is no more than <yearDiff>
    -- //              (iv) not friends
    -- //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    -- //            the containing album of each photo in which they are tagged together

      -- basically we're trying to suggest pairs of users who are likely to be friends
      -- because they have common tagged posts


-- SELECT U1.user_ID, U1.First_Name, U1.last_name, U1.year_of_birth, U2.user_ID, U2.First_Name, U2.last_name, U2.year_of_birth
-- FROM project2.Public_Users U1
-- JOIN project2.Public_Users U2 on U2.gender = U1.gender
-- WHERE U1.year_of_birth IS NOT NULL AND U2.year_of_birth IS NOT NULL
--      AND U1.gender IS NOT NULL AND U2.gender IS NOT NULL
--      AND (ABS(U1.year_of_birth - U2.year_of_birth) < 10) -- need to change to yearDiff in jdbc
-- AND EXISTS ( -- fixed error because one tag can't equal more than one ID
--     SELECT 1
--     FROM project2.Public_Tags T1
--     JOIN project2.Public_Tags T2 ON T1.Tag_Photo_ID = T2.Tag_Photo_ID
--     WHERE T1.Tag_Subject_ID = U1.user_ID AND T2.Tag_Subject_ID = U2.user_ID
-- )
-- AND NOT EXISTS (
--     SELECT 1 
--     FROM project2.Public_FRIENDS F
--     WHERE (U1.user_ID = F.user1_ID AND U2.user_ID = F.user2_ID)
--        OR (U1.user_ID = F.user2_ID AND U2.user_ID = F.user1_ID)
-- )
-- ORDER BY U1.user_id 
-- FETCH FIRST 1 ROWS ONLY; -- change to num in jdbc


-- This one is so hard. Def need to look more into it, but also might be working...we just don't know the exact values
-- punched in for the "correct" solution. 

/*
one thought. I know prof talked about how we're graded on performance
I feel like creating and dropping view everytime may slow it down.
No idea if this will matter or not but it came to mind while looking at the code
*/


CREATE VIEW top_pairs_view AS
SELECT
    U1.user_id         AS user_id1,
    U1.first_name      AS first_name1,
    U1.last_name       AS last_name1,
    U1.year_of_birth   AS year_of_birth1,
    U2.user_id         AS user_id2,
    U2.first_name      AS first_name2,
    U2.last_name       AS last_name2,
    U2.year_of_birth   AS year_of_birth2
    -- I think we should add in this line which counts the num of photos that both users are tagged in
    -- COUNT(DISTINCT T1.tag_photo_id) AS shared_photos
FROM project2.Public_Users U1
-- ensuring same gender
JOIN project2.Public_Users U2
       ON U1.gender = U2.gender
-- has to be different users
      AND U1.user_id < U2.user_id
WHERE U1.year_of_birth IS NOT NULL
  AND U2.year_of_birth IS NOT NULL
  -- making sure birth year is valid and within limit
  AND ABS(U1.year_of_birth - U2.year_of_birth) <= 30 -- chnage to ? in jdbc
  /*
CHANGE TO: yearDiff instead of 30 ^^^
  */
  -- checks if they are tagged in at least one common photo

  /*
I think this completely EXISTS subquery should be replaced with the following
This allows us to track how many shared photos there are

JOIN project2.Public_Tags T1
      ON T1.tag_subject_id = U1.user_id
JOIN project2.Public_Tags T2
      ON T2.tag_subject_id = U2.user_id
     AND T1.tag_photo_id = T2.tag_photo_id

For this below vv
  */
  AND EXISTS (
      SELECT 1
      FROM project2.Public_Tags T1
      JOIN project2.Public_Tags T2 
         ON T1.tag_photo_id = T2.tag_photo_id
      WHERE T1.tag_subject_id = U1.user_id
        AND T2.tag_subject_id = U2.user_id
  )
  -- making sure they aren't already friends
  AND NOT EXISTS (
      SELECT 1
      FROM project2.Public_Friends F
      WHERE (F.user1_id = U1.user_id AND F.user2_id = U2.user_id)
         OR (F.user2_id = U1.user_id AND F.user1_id = U2.user_id)
  )
ORDER BY U1.user_id, U2.user_id
FETCH FIRST 2 ROWS ONLY;   -- change to ? in jdbc
/*
CHANGE TO: num instead of 2 ^^^^
*/


-- finding common photos
SELECT
    tv.user_id1,
    tv.first_name1,
    tv.last_name1,
    tv.year_of_birth1,
    tv.user_id2,
    tv.first_name2,
    tv.last_name2,
    tv.year_of_birth2,
    p.photo_id,
    p.photo_link,
    a.album_id,
    a.album_name
FROM top_pairs_view tv
-- photos where both users are tagged
JOIN project2.Public_Tags t1
      ON t1.tag_subject_id = tv.user_id1
JOIN project2.Public_Tags t2
      ON t2.tag_subject_id = tv.user_id2
     AND t1.tag_photo_id = t2.tag_photo_id
-- retrieve photo link
JOIN project2.Public_Photos p
      ON p.photo_id = t1.tag_photo_id
-- retrieve album name
JOIN project2.Public_Albums a
      ON a.album_id = p.album_id
/*
I think this order by is wrong. This is what spec says:
The pairs of users should be reported in descending order by the number of photos in which the two users were tagged together.

Right now we're sorting by ID instead of number of shared photos

Think it should be this:
ORDER BY tv.shared_photos DESC, tv.user_id1 ASC, tv.user_id2 ASC, p.photo_id ASC;
*/
ORDER BY tv.user_id1, tv.user_id2, p.photo_id;



DROP VIEW top_pairs_view;
