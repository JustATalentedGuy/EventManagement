package system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

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
    public static boolean registerUser (String username, String password, String email, String userType) {
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

    public static ArrayList<Event> getListOfEventsFromUser(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM registrations WHERE userid = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("eventid")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

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

    public static ArrayList<Event> getListOfRegisteredEvents(int id) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM registration WHERE userid = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    events.add(getEvent(result.getInt("eventid")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

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

    

    




    


    // EVENT RELATED FUNCTIONS
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

    public static ArrayList<Event> getEventsByOrganizer(int id) {
    String query = "SELECT * FROM event WHERE organizer = ?";
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
        return events;
    }

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
        return events;
    }

    public static ArrayList<Event> getAllEvents() {
        String query = "SELECT * FROM event where approved = true and finished = false";
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
        return events;
    }

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

    public static ArrayList<Event> getListOfOrganizingRequests() {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE approved = false";
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
        return events;
    }







    // VENUE RELATED FUNCTIONS
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
    public static boolean isMaxReached(int eventID) {
        Event event = getEvent(eventID);
        return event.getMaxParticipants() == SystemManager.getListOfUsersFromEvent(eventID).size();
    }

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
