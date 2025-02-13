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



SELECT U1.user_ID, U1.First_Name, U1.last_name, U1.year_of_birth, U2.user_ID, U2.First_Name, U2.last_name, U2.year_of_birth
FROM project2.Public_Users U1
JOIN project2.Public_Users U2 on U2.gender = U1.gender
WHERE U1.year_of_birth IS NOT NULL AND U2.year_of_birth IS NOT NULL
     AND U1.gender IS NOT NULL AND U2.gender IS NOT NULL
     AND (ABS(U1.year_of_birth - U2.year_of_birth) < 10) -- need to change to yearDiff in jdbc
AND EXISTS ( -- fixed error because one tag can't equal more than one ID
    SELECT 1
    FROM project2.Public_Tags T1
    JOIN project2.Public_Tags T2 ON T1.Tag_Photo_ID = T2.Tag_Photo_ID
    WHERE T1.Tag_Subject_ID = U1.user_ID AND T2.Tag_Subject_ID = U2.user_ID
)
AND NOT EXISTS (
    SELECT 1 
    FROM project2.Public_FRIENDS F
    WHERE (U1.user_ID = F.user1_ID AND U2.user_ID = F.user2_ID)
       OR (U1.user_ID = F.user2_ID AND U2.user_ID = F.user1_ID)
)
ORDER BY U1.user_id 
FETCH FIRST 1 ROWS ONLY; -- change to num in jdbc