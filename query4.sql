    -- // Query 4
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    -- //            <num> photos with the most tagged users
    -- //        (B) For each photo identified in (A), find the IDs, first names, and last names
    -- //            of the users therein tagged
SELECT P.Photo_ID, P.Photo_Link, A.Album_ID, A.Album_Name, COUNT(*) AS numTags
FROM project2.Public_Photos P
JOIN project2.Public_Albums A ON A.Album_ID = P.Album_ID
JOIN project2.Public_Tags T ON T.Tag_Photo_ID = P.Photo_ID
GROUP BY P.Photo_ID, P.Photo_Link, A.Album_ID, A.Album_Name
ORDER BY numTags DESC, A.Album_ID ASC
FETCH FIRST 5 ROWS ONLY; -- should be num, but tested on 5



-- needs help, but we can def implement a better way in jdbc
SELECT U.user_id, U.first_name, U.last_name
FROM project2.Public_Users U
JOIN project2.Public_Tags T ON T.Tag_Photo_ID = (
    SELECT P.Photo_ID
    FROM project2.Public_Photos P
    JOIN project2.Public_Albums A ON A.Album_ID = P.Album_ID
    JOIN project2.Public_Tags T ON T.Tag_Photo_ID = P.Photo_ID
    GROUP BY P.Photo_ID
    ORDER BY COUNT(*) DESC
    FETCH FIRST 1 ROWS ONLY
)
WHERE T.Tag_Subject_ID = U.user_ID;

