-- // Query 3
--     // -----------------------------------------------------------------------------------
--     // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
--     //            in their hometown (i.e. their current city and their hometown are different)

-- SELECT U.user_id, U.first_name, U.last_name
-- FROM project2.Public_Users U
-- -- any user with no current city entry
-- INNER JOIN project2.Public_User_Current_Cities C ON U.user_id = C.user_id
-- -- any user with no hometown entry
-- INNER JOIN project2.Public_User_Hometown_Cities H ON U.user_id = H.user_id
-- WHERE C.city_id != H.city_id;



SELECT U.user_id, U.first_name, U.last_name
FROM project2.Public_Users U
JOIN project2.Public_User_Current_Cities C ON U.user_id = C.user_id
JOIN project2.Public_User_Hometown_Cities H ON U.user_id = H.user_id
WHERE C.current_city_id IS NOT NULL AND H.hometown_city_id IS NOT NULL AND C.current_city_id <> H.hometown_city_id
ORDER BY U.user_id ASC;