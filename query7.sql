    -- // Query 7 - Completed
    -- // -----------------------------------------------------------------------------------
    -- // GOALS: (A) Find the name of the state or states in which the most events are held
    -- //        (B) Find the number of events held in the states identified in (A)

CREATE VIEW EventCount AS 
    SELECT C.State_Name, COUNT(*) AS StateCount
    FROM project2.Public_Cities C
    JOIN project2.Public_User_Events E ON E.event_city_id = C.city_id
    GROUP BY C.state_name;

SELECT State_Name
FROM EventCount
WHERE StateCount = (SELECT MAX(StateCount) FROM EventCount);

SELECT MAX(StateCount)
FROM EventCount;

DROP VIEW EventCount;




