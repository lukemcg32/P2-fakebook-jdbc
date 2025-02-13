-- total users with birth month info
SELECT COUNT(*) AS Birthed, Month_of_Birth
FROM project2.users
WHERE Month_of_Birth IS NOT NULL 
GROUP BY Month_of_Birth
ORDER BY Birthed DESC, Month_of_Birth ASC;

-- names of users born in the most popular month
SELECT User_ID, First_Name, Last_Name
FROM project2.users
WHERE Month_of_Birth = 11  --from solution, but worked into the query with java
ORDER BY User_ID ASC;

-- names of users bron in least popular month
SELECT User_ID, First_Name, Last_Name
FROM project2.users
WHERE Month_of_Birth = 2  --from solution, but worked into the query with java
ORDER BY User_ID ASC;