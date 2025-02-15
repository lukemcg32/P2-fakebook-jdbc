    -- // Query 9 - Complete
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find all pairs of users that meet each of the following criteria
    -- //              (i) same last name
    -- //              (ii) same hometown
    -- //              (iii) are friends
    -- //              (iv) less than 10 birth years apart


SELECT U1.user_id, U1.first_name, U1.last_name, U2.user_id, U2.first_name, U2.last_name
FROM project2.Public_Users U1
JOIN project2.Public_Users U2 ON U1.user_id != U2.user_id
JOIN project2.Public_FRIENDS F ON (U1.user_id = F.user1_id AND U2.user_id = F.user2_id)
                                OR (U2.user_id = F.user1_id AND U1.user_id = F.user2_id)
JOIN project2.Public_User_Hometown_Cities HC1 ON HC1.user_id = U1.user_id
JOIN project2.Public_User_Hometown_Cities HC2 ON HC2.user_id = U2.user_id
WHERE HC1.hometown_city_id = HC2.hometown_city_id 
    AND U1.last_name = U2.last_name 
    AND (ABS(U1.year_of_birth - U2.year_of_birth) < 10)
    AND U1.user_id < U2.user_id;