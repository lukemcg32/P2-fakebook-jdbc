    -- // Query 4 - Completed
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    -- //            <num> photos with the most tagged users
    -- //        (B) For each photo identified in (A), find the IDs, first names, and last names
    -- //            of the users therein tagged

-- part a
CREATE VIEW Q4_View AS
SELECT P.Photo_ID, P.Photo_Link, A.Album_ID, A.Album_Name, COUNT(*) AS numTags
FROM project2.Public_Photos P
JOIN project2.Public_Albums A ON A.Album_ID = P.Album_ID
JOIN project2.Public_Tags T ON T.Tag_Photo_ID = P.Photo_ID
GROUP BY P.Photo_ID, P.Photo_Link, A.Album_ID, A.Album_Name
ORDER BY numTags DESC, A.Album_ID ASC
FETCH FIRST 5 ROWS ONLY; -- ? in JDBC



SELECT Photo_ID, Photo_Link, Album_ID, Album_Name
FROM Q4_View;


-- Now part b
-- this works, just unordered
SELECT T.Tag_Photo_ID, U.user_id, U.first_name, U.last_name
FROM project2.Public_Users U
JOIN project2.Public_Tags T ON T.TAG_SUBJECT_ID = U.user_id
WHERE T.TAG_PHOTO_ID IN (SELECT Photo_ID FROM Q4_View)
ORDER BY T.Tag_Photo_ID ASC, U.user_id ASC;


DROP VIEW Q4_View;