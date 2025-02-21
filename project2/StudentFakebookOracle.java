/*
Luke McGuiness and Sam Kaminski
P2 for 484
Winter 25
*/

package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }

    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                    "SELECT COUNT(*) AS Birthed, Month_of_Birth " + // select birth months and number of uses with that birth month
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth IS NOT NULL " + // for which a birth month is available
                            "GROUP BY Month_of_Birth " + // group into buckets by birth month
                            "ORDER BY Birthed DESC, Month_of_Birth ASC"); // sort by users born in that month, descending; break ties by birth month

            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    mostMonth = rst.getInt(2); //   it is the month with the most
                }
                if (rst.isLast()) { // if last record
                    leastMonth = rst.getInt(2); //   it is the month with the least
                }
                total += rst.getInt(1); // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);

            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + mostMonth + " " + // born in the most popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + leastMonth + " " + // born in the least popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }

    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        //trying this to see if it fixes error in ag:

        FirstNameInfo info = new FirstNameInfo();
        int mostCommonNameCount = 0;

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            // (a) find longest first names
            ResultSet rs = stmt.executeQuery( // rs stores query result
            // our query from query1.sql
            "SELECT DISTINCT first_name " +
            "FROM project2.Public_Users " +
            "WHERE length(first_name) = (" +
            // Subquery below
            "SELECT MAX(length(first_name)) " +
            "FROM project2.Public_Users " +
            ") "                            +
            "AND first_name IS NOT NULL " +
            //add this to order correctly
            "ORDER BY first_name ASC");

            //process the results
            while (rs.next()) {
                info.addLongName(rs.getString("first_name"));
            }

            //rs.close();
            
            // (b) find shortest first names
            rs = stmt.executeQuery(
            "SELECT DISTINCT first_name " +
            "FROM project2.Public_Users " +
            "WHERE length(first_name) =  (" +
            // subquery below
            "SELECT MIN(length(first_name)) " +
            "FROM project2.Public_Users " +
            ") "                            +
            "AND first_name IS NOT NULL "+
            //add this to order correctly
            "ORDER BY first_name ASC");
            
            while (rs.next()) {
                info.addShortName(rs.getString("first_name"));
            }

            //rs.close();

            // (c) find count of most common first name
            rs = stmt.executeQuery(
            "SELECT COUNT(*) AS mostName " +
            "FROM project2.Public_Users " +
            "WHERE First_Name IS NOT NULL " +
            "GROUP BY First_Name " +
            "ORDER BY mostName DESC " +
            "FETCH FIRST 1 ROW ONLY");
            
            if (rs.next()) {
                mostCommonNameCount = rs.getInt("mostName");
                info.setCommonNameCount(mostCommonNameCount);
            }

            //rs.close();
            
            // (d) find the most common first name
            rs = stmt.executeQuery(
            "SELECT first_name " +
            "FROM project2.Public_Users " +
            "WHERE First_Name IS NOT NULL " +
            "GROUP BY First_Name " +
            "HAVING COUNT(*) = " + mostCommonNameCount + " " +  // uses the value from part c
            //dont need to include subquery here bc we have this variable from part c
            "ORDER BY first_name ASC");

            while (rs.next()) {
                info.addCommonName(rs.getString("first_name"));
            }

            rs.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                FirstNameInfo info = new FirstNameInfo();
                info.addLongName("Aristophanes");
                info.addLongName("Michelangelo");
                info.addLongName("Peisistratos");
                info.addShortName("Bob");
                info.addShortName("Sue");
                info.addCommonName("Harold");
                info.addCommonName("Jessica");
                info.setCommonNameCount(42);
                return info;
            */
            return info; 
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            
            return new FirstNameInfo();
        }
    }

    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            // (a) find user with no friends
            ResultSet rs = stmt.executeQuery(
            // our query from query2.sql
            "SELECT U.user_id, U.first_name, U.last_name " +
            "FROM project2.Public_Users U " +
            "WHERE NOT EXISTS " +
            "(SELECT 1 FROM project2.Public_Friends F " +
            " WHERE U.user_id = F.user1_id OR U.user_id = F.user2_id " + 
            ")"                                     +
            "ORDER BY U.user_id ASC");

            // we must retrieve user id, first name, and last name
            // with this create the UserInfo object like in example
            while (rs.next()) {
                // should we use long instead of int?
                long userid = rs.getLong("user_id");
                String firstNm = rs.getString("first_name"); 
                String lastNm = rs.getString("last_name"); 

                UserInfo lonelyUser = new UserInfo(userid, firstNm, lastNm);
                results.add(lonelyUser); // add user object to results list
            }
            
            rs.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            // (a) Find the IDs, first names, and last names of users who no longer live
            //in their hometown (i.e. their current city and their hometown are different)
            ResultSet rs = stmt.executeQuery(
            //our query from query3.sql
            "SELECT U.user_id, U.first_name, U.last_name " +
            "FROM project2.Public_Users U " +
            "JOIN project2.Public_User_Current_Cities C ON U.user_id = C.user_id " +
            "JOIN project2.Public_User_Hometown_Cities H ON U.user_id = H.user_id " +
            "WHERE C.current_city_id IS NOT NULL " +
            "AND H.hometown_city_id IS NOT NULL " +
            "AND C.current_city_id <> H.hometown_city_id " +
            "ORDER BY U.user_id ASC"
        );

            //process it now
            //for each tuple create a UserInfo object. Add it to results list
            while (rs.next()) {
            long userid = rs.getLong("user_id");
            String firstNm = rs.getString("first_name");
            String lastNm = rs.getString("last_name");

            // UserInfo object with values
            UserInfo user = new UserInfo(userid, firstNm, lastNm);
            results.add(user);
        }

        rs.close();
            
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */
        } catch (SQLException e) {
            // can I just comment it out?
            // System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            //have to use a ddl to create the view
            //do this instead of a normal query, bc we don't want to return this data
            // (a) Find the IDs, links, and IDs and names of the containing album of the top
            //<num> photos with the most tagged users
            
            //create OR REPLACE the view -
            stmt.executeUpdate(
            "CREATE OR REPLACE VIEW Q4_View AS " +
            "SELECT P.Photo_ID, P.Photo_Link, A.Album_ID, A.Album_Name, COUNT(*) AS numTags " +
            "FROM project2.Public_Photos P " +
            "JOIN project2.Public_Albums A ON A.Album_ID = P.Album_ID " +
            "JOIN project2.Public_Tags T ON T.Tag_Photo_ID = P.Photo_ID " +
            "GROUP BY P.Photo_ID, P.Photo_Link, A.Album_ID, A.Album_Name " +
            "ORDER BY numTags DESC, P.Photo_ID ASC " +
            "FETCH FIRST " + num + " ROWS ONLY"
        );

            //now retrieve details using resultset
            ResultSet rsPhotos = stmt.executeQuery(
            "SELECT Photo_ID, Photo_Link, Album_ID, Album_Name FROM Q4_View"
            );

            while (rsPhotos.next()) {
                //now get photoid, link, albumid, albumname
                long photoId = rsPhotos.getLong("Photo_ID");
                String photoLink = rsPhotos.getString("Photo_Link");
                long albumId = rsPhotos.getLong("Album_ID");
                String albumName = rsPhotos.getString("Album_Name");

                // PhotoInfo object
                PhotoInfo photo = new PhotoInfo(photoId, albumId, photoLink, albumName);
                //using PhotoInfo object, create TaggedPhotoInfo object
                TaggedPhotoInfo taggedPhoto = new TaggedPhotoInfo(photo);

                
                // part (b) now
                //(B) For each photo identified in (A), find the IDs, first names, and last names
                //of the users therein tagged
                    //create query to get users tagged in specific photo
                ResultSet rsUsers = stmt.executeQuery(
                    "SELECT U.user_id, U.first_name, U.last_name " +
                    "FROM project2.Public_Users U " +
                    "JOIN project2.Public_Tags T ON T.TAG_SUBJECT_ID = U.user_id " +
                    "WHERE T.TAG_PHOTO_ID = " + photoId + " " +
                    //users in ascending order
                    "ORDER BY U.user_id ASC"
                );
                while (rsUsers.next()) {
                    //get userid, first, and last name
                    long userId = rsUsers.getLong("user_id");
                    String firstName = rsUsers.getString("first_name");
                    String lastName = rsUsers.getString("last_name");
                    //now create a user with that info
                    UserInfo user = new UserInfo(userId, firstName, lastName);
                    //add user object to this
                    taggedPhoto.addTaggedUser(user);
                }
                rsUsers.close();

                //add the taggedphotoinfo object to result list, which we return
                results.add(taggedPhoto);
            }

            //remember to close every result set
            rsPhotos.close();

            //drop view
                //need this for points
            stmt.executeUpdate("DROP VIEW Q4_View");

            
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
            FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");
    
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                                                    FakebookOracleConstants.ReadOnly)) {

            // Use CREATE OR REPLACE so we don't get ORA-00955 if the view already exists.
            stmt.executeUpdate( 
                "CREATE OR REPLACE VIEW top_pairs_view AS " +
                "SELECT " +
                "    U1.user_id         AS user_id1, " +
                "    U1.first_name      AS first_name1, " +
                "    U1.last_name       AS last_name1, " +
                "    U1.year_of_birth   AS year_of_birth1, " +
                "    U2.user_id         AS user_id2, " +
                "    U2.first_name      AS first_name2, " +
                "    U2.last_name       AS last_name2, " +
                "    U2.year_of_birth   AS year_of_birth2 " +
                "FROM project2.Public_Users U1 " +
                "JOIN project2.Public_Users U2 " +
                "  ON U1.gender = U2.gender " +
                " AND U1.user_id < U2.user_id " +
                "WHERE U1.year_of_birth IS NOT NULL " +
                "  AND U2.year_of_birth IS NOT NULL " +
                "  AND ABS(U1.year_of_birth - U2.year_of_birth) <= " + yearDiff + " " +
                "  AND EXISTS ( " +
                "      SELECT 1 FROM project2.Public_Tags T1 " +
                "      JOIN project2.Public_Tags T2 ON T1.tag_photo_id = T2.tag_photo_id " +
                "      WHERE T1.tag_subject_id = U1.user_id " +
                "        AND T2.tag_subject_id = U2.user_id " +
                "  ) " +
                "  AND NOT EXISTS ( " +
                "      SELECT 1 FROM project2.Public_Friends F " +
                "      WHERE (F.user1_id = U1.user_id AND F.user2_id = U2.user_id) " +
                "         OR (F.user2_id = U1.user_id AND F.user1_id = U2.user_id) " +
                "  ) " +
                "ORDER BY U1.user_id, U2.user_id " +
                "FETCH FIRST " + num + " ROWS ONLY"
            );
        
            ResultSet rs = stmt.executeQuery(
                "SELECT " +
                "    tv.user_id1, tv.first_name1, tv.last_name1, tv.year_of_birth1, " +
                "    tv.user_id2, tv.first_name2, tv.last_name2, tv.year_of_birth2, " +
                "    p.photo_id, p.photo_link, a.album_id, a.album_name " +
                "FROM top_pairs_view tv " +
                "JOIN project2.Public_Tags t1 ON t1.tag_subject_id = tv.user_id1 " +
                "JOIN project2.Public_Tags t2 ON t2.tag_subject_id = tv.user_id2 " +
                "  AND t1.tag_photo_id = t2.tag_photo_id " +
                "JOIN project2.Public_Photos p ON p.photo_id = t1.tag_photo_id " +
                "JOIN project2.Public_Albums a ON a.album_id = p.album_id " +
                "ORDER BY tv.user_id1, tv.user_id2, p.photo_id"
            );


            // group consecutive rows with the same pair
            MatchPair currentPair = null;
            long currentUid1 = -1;
            long currentUid2 = -1;

            // start looping
            while (rs.next()) {
                long uid1 = rs.getLong("user_id1");
                long uid2 = rs.getLong("user_id2");
                // if row belongs to a new pair, create a new MatchPair.
                if (currentPair == null || uid1 != currentUid1 || uid2 != currentUid2) {
                    UserInfo u1 = new UserInfo(uid1, rs.getString("first_name1"), rs.getString("last_name1"));
                    UserInfo u2 = new UserInfo(uid2, rs.getString("first_name2"), rs.getString("last_name2"));
                    long year1 = rs.getLong("year_of_birth1");
                    long year2 = rs.getLong("year_of_birth2");
                    currentPair = new MatchPair(u1, year1, u2, year2);
                    results.add(currentPair);
                    currentUid1 = uid1;
                    currentUid2 = uid2;
                } // if

                // Add the shared photo for this pair.
                long photoId = rs.getLong("photo_id");
                String photoLink = rs.getString("photo_link");
                long albumId = rs.getLong("album_id");
                String albumName = rs.getString("album_name");
                PhotoInfo photo = new PhotoInfo(photoId, albumId, photoLink, albumName);
                currentPair.addSharedPhoto(photo);
            }

        rs.close();

        // drop
        stmt.executeUpdate("DROP VIEW top_pairs_view");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        
        return results;
    }

        @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");
    
    try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                                                    FakebookOracleConstants.ReadOnly)) {

        // Create or replace the MutualPairs view to avoid the ORA-00955 error.
        stmt.executeUpdate(
            "CREATE OR REPLACE VIEW MutualPairs AS " +
            "WITH FriendsOf AS ( " +
            "    SELECT user1_id AS user_id, user2_id AS friend_id FROM project2.Public_Friends " +
            "    UNION ALL " +
            "    SELECT user2_id AS user_id, user1_id AS friend_id FROM project2.Public_Friends " +
            "), " +
            "PairMutuals AS ( " +
            "    SELECT F1.user_id AS id1, F2.user_id AS id2, F1.friend_id AS mutual_id " +
            "    FROM FriendsOf F1 " +
            "    JOIN FriendsOf F2 ON F1.friend_id = F2.friend_id " +
            "      AND F1.user_id < F2.user_id " +
            ") " +
            "SELECT id1, id2, COUNT(DISTINCT mutual_id) AS num_mutuals " +
            "FROM PairMutuals " +
            "GROUP BY id1, id2"
        );
        
        // Retrieve the top num pairs
        ResultSet rs = stmt.executeQuery(
            "SELECT M.id1, M.id2, M.num_mutuals, " +
            "       U1.first_name AS first_name1, U1.last_name AS last_name1, " +
            "       U2.first_name AS first_name2, U2.last_name AS last_name2 " +
            "FROM MutualPairs M " +
            "JOIN project2.Public_Users U1 ON U1.user_id = M.id1 " +
            "JOIN project2.Public_Users U2 ON U2.user_id = M.id2 " +
            "ORDER BY M.num_mutuals DESC " +
            "FETCH FIRST " + num + " ROWS ONLY"
        );


            while (rs.next()) {
                long id1 = rs.getLong("id1");
                long id2 = rs.getLong("id2");
                UserInfo u1 = new UserInfo(id1, rs.getString("first_name1"), rs.getString("last_name1"));
                UserInfo u2 = new UserInfo(id2, rs.getString("first_name2"), rs.getString("last_name2"));
                UsersPair pair = new UsersPair(u1, u2);
                
                // For this pair, retrieve all mutual friends sorted by user_id ascending.
                ResultSet rsMutuals = stmt.executeQuery(
                    "WITH FriendsOf AS ( " +
                    "    SELECT user1_id AS user_id, user2_id AS friend_id FROM project2.Public_Friends " +
                    "    UNION ALL " +
                    "    SELECT user2_id AS user_id, user1_id AS friend_id FROM project2.Public_Friends " +
                    "), " +
                    "PairMutuals AS ( " +
                    "    SELECT F1.user_id AS id1, F2.user_id AS id2, F1.friend_id AS mutual_id " +
                    "    FROM FriendsOf F1 " +
                    "    JOIN FriendsOf F2 ON F1.friend_id = F2.friend_id " +
                    "      AND F1.user_id < F2.user_id " +
                    ") " +
                    "SELECT U.user_id, U.first_name, U.last_name " +
                    "FROM PairMutuals PM " +
                    "JOIN project2.Public_Users U ON U.user_id = PM.mutual_id " +
                    "WHERE PM.id1 = " + id1 + " AND PM.id2 = " + id2 +
                    " ORDER BY U.user_id ASC"
                );

                while (rsMutuals.next()) {
                    long mutualID = rsMutuals.getLong("user_id");
                    String mFirst = rsMutuals.getString("first_name");
                    String mLast = rsMutuals.getString("last_name");
                    UserInfo mutual = new UserInfo(mutualID, mFirst, mLast);
                    pair.addSharedFriend(mutual);
                }

                results.add(pair);
            } // while

        rs.close();
        stmt.executeUpdate("DROP VIEW MutualPairs"); // Drop
        
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }

    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            // (A) Find the name of the state or states in which the most events are held
            // again, use ddl to create view
            //groups event by state
            stmt.executeUpdate(
            "CREATE VIEW EventCount AS " +
            "SELECT C.State_Name, COUNT(*) AS StateCount " +
            "FROM project2.Public_Cities C " +
            "JOIN project2.Public_User_Events E ON E.event_city_id = C.city_id " +
            "GROUP BY C.State_Name"
        );

            //query to retrieve highest event count from view
            ResultSet rsCount = stmt.executeQuery(
                "SELECT MAX(StateCount) AS MaxCount FROM EventCount"
            );
            //store this val in maxCount
            long maxCount = -1;
            if (rsCount.next()) {
                maxCount = rsCount.getLong("MaxCount");
            }
            rsCount.close();

            //return this later
            EventStateInfo info = new EventStateInfo(maxCount);

            //(B) Find the number of events held in the states identified in (A)
            //now create actual query
                //this selects state names from view where eventCount == maxCount
            ResultSet rsStates = stmt.executeQuery(
                "SELECT State_Name " +
                "FROM EventCount " +
                "WHERE StateCount = (SELECT MAX(StateCount) FROM EventCount) " +
                //order by alphabetically
                "ORDER BY State_Name ASC"
            );

            while (rsStates.next()) {
                //create object for each state event
                String stateName = rsStates.getString("State_Name");
                info.addState(stateName);
            }
            rsStates.close();

            //remember to drop all views
            stmt.executeUpdate("DROP VIEW EventCount");

            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */
            return info; // update this
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        //declare this before using
        AgeInfo result = null;
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            //(A) Find the ID, first name, and last name of the oldest friend of the user
            //with User ID <userID>
            //create view from query8.sql
                //this collects all friends of a user wither their birth info
            stmt.executeUpdate(
                "CREATE VIEW FindAges AS " +
                "SELECT U.user_id, U.first_name, U.last_name, U.year_of_birth, U.month_of_birth, U.day_of_birth " +
                "FROM project2.Public_Users U " +
                "JOIN project2.Public_FRIENDS F ON (F.user1_id = U.user_id OR F.user2_id = U.user_id) " +
                "WHERE " + userID + " IN (F.user1_id, F.user2_id)"
            );

            //query to find oldest friend
            ResultSet rsOldest = stmt.executeQuery(
                "SELECT user_id, first_name, last_name " +
                "FROM FindAges " +
                //this order by allows us to put the oldest friend first
                "ORDER BY year_of_birth ASC, month_of_birth ASC, day_of_birth ASC, user_id DESC " +
                "FETCH FIRST 1 ROW ONLY"
            );
            //oldest friend object
            UserInfo oldest = null;
            if (rsOldest.next()) {
                //get oldest friends userid, first, and last name
                long id = rsOldest.getLong("user_id");
                String first = rsOldest.getString("first_name");
                String last = rsOldest.getString("last_name");
                //fill object with info
                oldest = new UserInfo(id, first, last);
            }
            rsOldest.close();


            //B) Find the ID, first name, and last name of the youngest friend of the user
            //with User ID <userID>
            //now, still use view to get youngest friend
            ResultSet rsYoungest = stmt.executeQuery(
                "SELECT user_id, first_name, last_name " +
                "FROM FindAges " +
                //this allows us to put the youngest friend first
                "ORDER BY year_of_birth DESC, month_of_birth DESC, day_of_birth DESC, user_id DESC " +
                "FETCH FIRST 1 ROW ONLY"
            );
            //youngest friend object
            UserInfo youngest = null;
            if (rsYoungest.next()) {
                //get youngest friends userid, first, and last name
                long id = rsYoungest.getLong("user_id");
                String first = rsYoungest.getString("first_name");
                String last = rsYoungest.getString("last_name");
                //update object with this info
                youngest = new UserInfo(id, first, last);
            }
            rsYoungest.close();

            // drop the view
            stmt.executeUpdate("DROP VIEW FindAges");
            
            // Create the AgeInfo object with the oldest and youngest friends.
            result = new AgeInfo(oldest, youngest);
            
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo old = new UserInfo(12000000, "Galileo", "Galilei");
                UserInfo young = new UserInfo(80000000, "Neil", "deGrasse Tyson");
                return new AgeInfo(old, young);
            */
            return result;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }

    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            
            //(a) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
            // query from query9.sql
            ResultSet rs = stmt.executeQuery(
            "SELECT U1.user_id, U1.first_name, U1.last_name, " +
            "U2.user_id AS user2_id, U2.first_name AS user2_first, U2.last_name AS user2_last " +
            "FROM project2.Public_Users U1 " +
            "JOIN project2.Public_Users U2 ON U1.user_id != U2.user_id " +
            "JOIN project2.Public_FRIENDS F ON (U1.user_id = F.user1_id AND U2.user_id = F.user2_id) " +
            "OR (U2.user_id = F.user1_id AND U1.user_id = F.user2_id) " +
            "JOIN project2.Public_User_Hometown_Cities HC1 ON HC1.user_id = U1.user_id " +
            "JOIN project2.Public_User_Hometown_Cities HC2 ON HC2.user_id = U2.user_id " +
            "WHERE HC1.hometown_city_id = HC2.hometown_city_id " +
            "AND U1.last_name = U2.last_name " +
            "AND (ABS(U1.year_of_birth - U2.year_of_birth) < 10) " +
            "AND U1.user_id < U2.user_id " +
            //add this order by to fix ordering issue in ag
            "ORDER BY U1.user_id ASC, U2.user_id ASC");

            while (rs.next()) {
                //for each tuple, extract userid, first and last name
                long user1id = rs.getLong("user_id");
                String user1First = rs.getString("first_name");
                String user1Last = rs.getString("last_name");

                long user2id = rs.getLong("user2_id");
                String user2First = rs.getString("user2_first");
                String user2Last = rs.getString("user2_last");

                //create objects to store data together
                UserInfo u1 = new UserInfo(user1id, user1First, user1Last);
                UserInfo u2 = new UserInfo(user2id, user2First, user2Last);

                //sotre sibling pair
                SiblingInfo siblingPair = new SiblingInfo(u1, u2);
                results.add(siblingPair);
            }

            rs.close();
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(81023, "Kim", "Kardashian");
                UserInfo u2 = new UserInfo(17231, "Kourtney", "Kardashian");
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            */
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
