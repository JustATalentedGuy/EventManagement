package system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import venue.Department;
import venue.Venue;
import events.*;
import users.Admin;
import users.Organizer;
import users.User;
import users.Viewer;

public abstract class SystemManager {

    private static Connection con;

    static {
        final String url = "jdbc:mysql://localhost:3306/eventmanagement?useSSL=true&serverTimezone=UTC";
        final String user = "root";
        final String password = "Subash@2005";

        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // USER RELATED FUNCTIONS
    /**
     * Registers a new user in the database.
     *
     * @param username  the user's username; must not be null
     * @param password  the user's password; must not be null
     * @param email     the user's email; must not be null
     * @param userType  the user's type (e.g., "Admin", "Viewer"); must not be null
     * @return          true if registration succeeds; false otherwise
     */
    public static boolean registerUser (String username, String password, String email, String userType) {
        if (username == null || password == null || email == null || userType == null) {
            return false;
        }
        String query = "INSERT INTO users (username, password, email, usertype) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, userType);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return true;
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return  an ArrayList of User objects representing all users in the database
     * or an empty ArrayList if no users are found
     * 
     */
    public static ArrayList<User> getAllUsers() {

        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    String email = result.getString("email");
                    String userType = result.getString("userType");
                    User user = null;
                    switch (userType) {
                        case "Admin":
                            user = new Admin(id, username, password);
                            break;
                        case "Organizer":
                            user = new Organizer(id, username, password, email);
                            break;
                        case "Viewer":
                            user = new Viewer(id, username, password, email);
                            break;
                        default:
                            break;
                    }
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 
     * Registers the user to the event with the given ID.
     * 
     * @param userID  the ID of the user to retrieve
     * @return        the User object representing the user with the given ID, or null if no user is found
     * 
    */
    public static boolean registerUserToEvent(int userID, int eventID) {
        String query = "INSERT INTO registration (user_id, event_id) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, eventID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes the user with the given ID from the database.
     * 
     * @param userID  the ID of the user to remove
     * @param eventID the ID of the event to remove the user from
     * @return        true if the user was successfully removed, false otherwise
     * 
     */
    public static boolean removeUserRegistrationFromEvent(int userID, int eventID) {
        String query = "DELETE FROM registration WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, eventID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the user with the given ID from the database.
     * @param username username of the user to retrieve
     * @param password password of the user to retrieve
     * @param userType user type of the user to retrieve
     * @return the User object representing the user with the given ID, or null if no user is found
     */
    public static int validateUser(String username, String password, String userType) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND userType = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, userType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return -1;
            

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** 
     * Retrieves the user with the given ID from the database.
     * 
     * @param id the ID of the user to retrieve
     * @return the User object representing the user with the given ID, or null if no user is found
    */
    public static User getUser(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    String type = result.getString("userType");
                    if (type.equals("Organizer")) {
                        return new Organizer(id, result.getString("username"), result.getString("password"), result.getString("email"));
                    }
                    else if (type.equals("Viewer")) {
                        return new Viewer(id, result.getString("username"), result.getString("password"), result.getString("email"));
                    }
                    else if (type.equals("Admin")) {
                        return new Admin(id, result.getString("username"), result.getString("password"));
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     * Updates the user with the given ID from the database.
     * 
     * @param id the ID of the user to retrieve
     * @param variable the variable to update
     * @param variableValue the value to update the variable to
     * @return the User object representing the user with the given ID, or null if no user is found
    */
    public static boolean updateUser(int id, String variable, String variableValue) {
        // username, password, email
        String sql = "UPDATE users SET " + variable + " = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, variableValue);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     *  Gets the list of Organized Events for the Organizer with the given ID.
     * 
     * @param id the ID of the Organizer
     * @return the list of Organized Events for the Organizer with the given ID
    */
    public static ArrayList<Event> getListOfOrganizedEventsForOrganizer(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE organizer = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    /** 
     *   Gets the list of Events that the User with the given ID is attending.
     * 
     * @param id the ID of the User
     * @return the list of Events that the User with the given ID is attending
    */
    public static ArrayList<Event> getListOfRegisteredEvents(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM registration WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("event_id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    /** 
     * Checks whether the user with the given ID is registered to the event with the given ID.
     * 
     * @param userID the ID of the user
     * @param eventID the ID of the event
     * @return true if the user is registered to the event, false otherwise
    */
    public static boolean userIsRegisteredToEvent(int userID, int eventID) {
        String query = "SELECT * FROM registration WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, eventID);
            try (ResultSet result = stmt.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 
     * Gets the position of the user with the given ID in the waiting list for the event with the given ID.
     * 
     * @param userID the ID of the user
     * @param eventID the ID of the event
     * @return the position of the user in the waiting list, or 0 if the user is not in the waiting list
    */
    public static int userPositionInWaitingList(int userID, int eventID) {
        String query = "SELECT * FROM registration WHERE event_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, eventID);
            try (ResultSet result = stmt.executeQuery()) {
                int count = 0;
                while (result.next()) {
                    count++;
                }
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** 
     * Checks if the event with the given ID is full.
     * 
     * @param eventID the ID of the event
     * @param userID the ID of the user
     * @return true if the event is full, false otherwise
    */
    public static boolean isFavourite(int userID, int eventID) {
        String query = "SELECT * FROM favourite WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, eventID);
            try (ResultSet result = stmt.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 
     * Adds the event to the favourites of the user with the given ID.
     * 
     * @param userID the ID of the user
     * @param eventID the ID of the event
     * @return true if the event was added to the favourites, false otherwise
    */
    public static boolean addToFavourites(int userID, int eventID) {
        String query = "INSERT INTO favourite (user_id, event_id) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, eventID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Removes the event from the favourites of the user with the given ID.
     * 
     * @param userID the ID of the user
     * @param eventID the ID of the event
     * @return true if the event was removed from the favourites, false otherwise
    */
    public static boolean removeFromFavourites(int userID, int eventID) {
        String query = "DELETE FROM favourite WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, eventID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     *  Checks if the event with the given ID is in the favourites of the user with the given ID.
     * 
     * @param userID the ID of the user
     * @param eventID the ID of the event
     * @return true if the event is in the favourites, false otherwise
    */
    public static ArrayList<Event> getListOfFavouriteEvents(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM favourite WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("event_id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    

    




    


    // EVENT RELATED FUNCTIONS
    /** \
     * Gets the event with the given ID.
     * 
     * @param id the ID of the event
     * @return the event with the given ID, or null if no event with the given ID exists
    */
    public static int createEvent(String name, String description, int venue, int department, int organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved, String eventType, String field1, String field2) {
        String query = "INSERT INTO event (name, description, venue, department, organizer, starttime, endtime, maxparticipants, online, finished, approved, eventType, field1, field2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, venue);
            stmt.setInt(4, department);
            stmt.setInt(5, organizer);
            stmt.setString(6, startTime);
            stmt.setString(7, endTime);
            stmt.setInt(8, maxParticipants);
            stmt.setBoolean(9, online);
            stmt.setBoolean(10, finished);
            stmt.setBoolean(11, approved);
            stmt.setString(12, eventType);
            stmt.setString(13, field1);
            stmt.setString(14, field2);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0)  {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** 
     * Gets the event with the given ID.
     * 
     * @param id the ID of the event
     * @return the event with the given ID, or null if no event with the given ID exists
    */
    public static Event getEvent(int id) {
        String query = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    String eventType = result.getString("eventType");
                    if (eventType.equals("Competition")) {
                        return new Competition(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("description"),
                                getVenue(result.getInt("venue")),
                                getDepartment(result.getInt("department")),
                                getOrganizer(result.getInt("organizer")),
                                result.getString("starttime"),
                                result.getString("endtime"),
                                result.getInt("maxparticipants"),
                                result.getBoolean("online"),
                                result.getBoolean("finished"),
                                result.getBoolean("approved"),
                                result.getBoolean("rejected"),
                                eventType,
                                Integer.parseInt(result.getString("field1")),
                                result.getString("field2")
                        );
                    } else if (eventType.equals("Seminar")) {
                        return new Seminar(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("description"),
                                getVenue(result.getInt("venue")),
                                getDepartment(result.getInt("department")),
                                getOrganizer(result.getInt("organizer")),
                                result.getString("starttime"),
                                result.getString("endtime"),
                                result.getInt("maxparticipants"),
                                result.getBoolean("online"),
                                result.getBoolean("finished"),
                                result.getBoolean("approved"),
                                result.getBoolean("rejected"),
                                eventType,
                                result.getString("field1"),
                                result.getString("field2")
                        );
                    } else if (eventType.equals("Workshop")) {
                        return new Workshop(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("description"),
                                getVenue(result.getInt("venue")),
                                getDepartment(result.getInt("department")),
                                getOrganizer(result.getInt("organizer")),
                                result.getString("starttime"),
                                result.getString("endtime"),
                                result.getInt("maxparticipants"),
                                result.getBoolean("online"),
                                result.getBoolean("finished"),
                                result.getBoolean("approved"),
                                result.getBoolean("rejected"),
                                eventType,
                                result.getString("field1"),
                                result.getString("field2")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     * Gets all events.
     * 
     * @return a list of all events
    */
    public static ArrayList<Event> getEventsByOrganizer(int id) {
    String query = "SELECT * FROM event WHERE organizer = ? and approved = true and rejected = false and starttime > CURRENT_TIMESTAMP";
        ArrayList<Event> events = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.sort(events);
        return events;
    }

    /** 
     * Gets all pending event requests.
     * 
     * @param id the id of the organizer
     * @return a list of all pending event requests\
     
    */
    public static ArrayList<Event> getPendingEventRequests(int id) {
        String query = "SELECT * FROM event WHERE organizer = ? AND approved = false AND rejected = false";
        ArrayList<Event> events = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.sort(events);
        return events;
    }

    /** 
     * Gets all events.
     * 
     * @return a list of all events
    */
    public static ArrayList<Event> getAllEvents() {
        String query = "SELECT * FROM event where approved = true AND starttime >= CURRENT_DATE";
        ArrayList<Event> events = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.sort(events);
        return events;
    }

    /** 
     * Gets an organizer.
     * 
     * @param id the id of the organizer
     * @return the organizer
    */
    public static Organizer getOrganizer(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Organizer(id, result.getString("username"), result.getString("password"), result.getString("email"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     * Get the waiting list for an event.
     * 
     * @param event_id the id of the event
     * @param max_participants the maximum number of participants
     * @return the waiting list
    */
    public static ArrayList<User> getWaitingList(int event_id, int max_participants) {
        String query = "SELECT * FROM registration WHERE event_id = ? ORDER BY registration_date ASC";
        ArrayList<User> users = new ArrayList<>();
    
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, event_id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    users.add(getUser(result.getInt("user_id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        if (users.size() <= max_participants) {
            return new ArrayList<>();
        }
        return new ArrayList<>(users.subList(max_participants, users.size()));
    }
    
    /** 
     * Checks if the user is registered for the event.
     * 
     * @param user_id the id of the user
     * @param event_id the id of the event
     * @return true if the user is registered for the event, false otherwise
    */
    public static boolean isUserRegistered(int user_id, int event_id, int max_participants) {
        ArrayList<User> allRegistrations = getListOfUsersFromEvent(event_id);
        if (allRegistrations.size() <= max_participants) {
            return allRegistrations.stream().anyMatch(user -> user.getUserID() == user_id);
        }
        return allRegistrations.subList(0, max_participants).stream().anyMatch(user -> user.getUserID() == user_id);
    }
    
    /** 
     *  Check if the user is in the waiting list for the event.
     * 
     * @param user_id the id of the user
     * @param event_id the id of the event
     * @param max_participants the maximum number of participants
     * @return true if the user is in the waiting list for the event, false otherwise

    */
    public static boolean isUserInWaitingList(int user_id, int event_id, int max_participants) {
        ArrayList<User> allRegistrations = getListOfUsersFromEvent(event_id);
        if (allRegistrations.size() <= max_participants) {
            return false;
        }
        return allRegistrations.subList(max_participants, allRegistrations.size()).stream()
                .anyMatch(user -> user.getUserID() == user_id);
    }

    /** 
     * Gets the list of users from an event.
     * 
     * @param id the id of the event
     * @return the list of users from the event
    */
    public static ArrayList<User> getListOfUsersFromEvent(int id) {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM registration WHERE event_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    users.add(getUser(result.getInt("user_id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /** 
     * Update a variable in the event database.
     * 
     * @param id the id of the event
     * @param variable the variable to update
     * @param newValue the new value of the variable
     * @return true if the update was successful, false otherwise
    */
    public static boolean updateEvent(int id, String variable, Object newValue) {
        String sql = "UPDATE event SET " + variable + " = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            if (newValue instanceof String) {
                stmt.setString(1, (String) newValue);
            } else if (newValue instanceof Integer) {
                stmt.setInt(1, (int) newValue);
            } else if (newValue instanceof Boolean) {
                stmt.setInt(1, (boolean) newValue ? 1 : 0);
            } else if (newValue instanceof Timestamp) {
                stmt.setTimestamp(1, (Timestamp) newValue);
            }

            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Delete an event from the database.
     * 
     * @param id the id of the event
     * @return true if the event was deleted, false otherwise
    */
    public static boolean deleteEvent(int id) {
        String query = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Reject an event.
     * 
     * @param id the id of the event
     * @return true if the event was rejected, false otherwise
    */
    public static boolean rejectEvent(int id) {
        String query = "UPDATE event SET rejected = true WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Get the list of events yet to be approved.
     * 
     * @return the list of events yet to be approved
    */
    public static ArrayList<Event> getListOfOrganizingRequests() {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE approved = false and rejected = false and starttime >= CURRENT_DATE";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
                return events;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(events);
        return events;
    }







    // VENUE RELATED FUNCTIONS
    /**
     * Get the venue with the given id.
     * 
     * @param id the id of the venue
     * @return the venue with the given id
    */
    public static Venue getVenue(int id) {
        String query = "SELECT * FROM venue WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Venue(id, result.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     * Get the venue with the given name.
     * 
     * @param name the name of the venue
     * @return the venue with the given name
    */
    public static Venue getVenue(String name) {
        String query = "SELECT * FROM venue WHERE name = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Venue(result.getInt("id"), result.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     * Register a venue.
     * 
     * @param name the name of the venue
     * @return the id of the venue
    */
    public static int registerVenue(String name) {
        String query = "INSERT INTO venue (name) VALUES (?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                    else {
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** 
     * Update a venue.
     * 
     * @param id the id of the venue
     * @param newName the new name of the venue
     * @return true if the venue was updated, false otherwise
    */
    public static boolean updateVenue(int id, String newName) {
        String sql = "UPDATE venue SET (name) = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     *  Delete a venue.
     * 
     * @param id the id of the venue
     * @return true if the venue was deleted, false otherwise
    */
    public static boolean deleteVenue(int id) {
        String sql = "DELETE FROM venue WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Get all venues.
     * 
     * @return the list of venues
    */
    public static ArrayList<Venue> getAllVenues() {
        ArrayList<Venue> venues = new ArrayList<>();
        String query = "SELECT * FROM venue";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    venues.add(new Venue(
                        result.getInt("id"),
                        result.getString("name")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venues;
    }

    /** 
     *  Get a venue by id.
     * 
     * @param id the id of the venue
     * @return the venue with the given id
    */
    public static ArrayList<Event> getEventsFromVenue(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE venueid = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
                return events;
            }
            catch (Exception e) {
            e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    // DEPARTMENT RELATED FUNCTIONS
    /** 
     *   Get a department by id.
     * 
     * @param id the id of the department
     * @return the department with the given id
    */
    public static Department getDepartment(int id) {
        String query = "SELECT * FROM department WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Department(id, result.getString("name"));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     *    Get all departments.
     * 
     * @return Get a department by name.
     * 
     * @param name the name of the department
     * @return the department with the given name
    */
    public static Department getDepartment(String name) {
        String query = "SELECT * FROM department WHERE name = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Department(result.getInt("id"), result.getString("name"));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 
     * Register a department.
     * 
     *  @param name the name of the department
     * @return the id of the department
     * 
    */
    public static int registerDepartment(String name) {
        String query = "INSERT INTO department (name) VALUES (?)";
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                    else {
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** 
     * Update a department.
     * 
     * @param id the id of the department
     * @param newName the new name of the department
     * @return true if the department was updated, false otherwise
    */
    public static boolean updateDepartment(int id, String newName) {
        String sql = "UPDATE department SET (name) = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Delete a department.
     * 
     * @param id the id of the department
     * @return true if the department was deleted, false otherwise
    */
    public static boolean deleteDepartment(int id) {
        String sql = "DELETE FROM department WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     *  Get all departments.
     * 
     * @return Get all departments.
    */
    public static ArrayList<Department> getAllDepartments() {
        ArrayList<Department> departments = new ArrayList<>();
        String query = "SELECT * FROM department";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    departments.add(getDepartment(result.getInt("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    /** 
     * Get all events from a department.
     * 
     * @param id the id of the department
     * @return Get all events from a department.
    */
    public static ArrayList<Event> getEventsFromDepartment(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE departmentid = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("id")));
                }
                return events;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }







    // Helper Events
    /**
     * Checks whether the maximum number of participants has been reached for an event.
     * @param eventID the ID of the event
     * @return true if the maximum number of participants has been reached, false otherwise
    */
    public static boolean isMaxReached(int eventID) {
        Event event = getEvent(eventID);
        return event.getMaxParticipants() == SystemManager.getListOfUsersFromEvent(eventID).size();
    }

    /** 
     * Checks whether there is a collision between two events.
     * 
     * @param startTime the start time of the event
     * @param endTime the end time of the event
     * @param venue the venue of the event
     * @param type the type of the event
     * @return true if there is a collision, false otherwise
    */
    public static int isCollision(String startTime, String endTime, String venue, boolean type) {
        ArrayList<Event> events = getAllEvents();
        for (Event event : events) {
            if (event.getVenue().getName().equals(venue) && !event.isOnline() && !type) {
                if (event.isCollision(startTime, endTime)) {
                    return event.getEventID();
                }
            }
        }
        return 0;
    }

    /** 
     * Checks whether a user is already registered.
     * 
     * @param email the email of the user
     * @return true if the user is already registered, false otherwise
    */
    public static boolean userAlreadyRegistered(String email) {
        ArrayList<User> users = getAllUsers();
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
}