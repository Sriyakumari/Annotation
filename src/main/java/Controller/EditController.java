package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

import Model.User;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

@Namespace("/api")
public class EditController extends ActionSupport {
    private User userBean;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private static Connection connection;
    private List<User> userList;


     public User getUserBean() {
        return userBean;
    }

    public void setUserBean(User user) {
        userBean = user;
    }

    public EditController() {
        super();
        userBean = new User(); // Initialize userBean
        initializeConnection2();
        // editUser();
       
    }
      public void initializeConnection2() {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("connectAndSaveData").start();
        String jdbcURL = "jdbc:postgresql://localhost:9001/kys";
        String dbUser = "postgres";
        String dbPassword = "redhat";

        try {
            logger.log(Level.INFO, "Connecting to the database-----------");
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            if (connection != null && !connection.isClosed()) {
                logger.log(Level.INFO, "Database connection is ready.");
                
            }

        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to the database", e);
        } finally {
            span.finish();
        }
    }

    public String editUser() {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.buildSpan("editUser").start();

        try {

            logger.log(Level.INFO, "Connecting to the database...EDITTTTT-------------");

            HttpServletRequest request = ServletActionContext.getRequest();
            logger.log(Level.INFO, "Request Method: " + request.getMethod());
            logger.log(Level.INFO, "Request Headers:");
            Collections.list(request.getHeaderNames())
                    .forEach(headerName -> logger.log(Level.INFO, headerName + ": " + request.getHeader(headerName)));
    
            // Ensure that the request content type is JSON
            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("application/json")) {
                // Get JSON data and set it to userBean
                setUserFromJson(request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual));

                // Retrieve the new username and password from the userBean
                String newUsername = userBean.getUsername();
                String newPassword = userBean.getPassword();

                // Update the user's information in the database
                updateUserInDatabase(newUsername, newPassword);

                return SUCCESS;
            } else {
                logger.log(Level.WARNING, "Invalid content type. Expected application/json.");
                // Return appropriate error response to the client
                return ERROR;
            }
        } catch (IOException | SQLException e) {
            logger.log(Level.SEVERE, "Error editing user", e);
            // Handle the exception appropriately
            return ERROR;
        } finally {
            span.finish();
        }
    }

    private void setUserFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Parse the JSON string to a User object
            User user = objectMapper.readValue(json, User.class);
            // Set the username and password from the parsed User object
            userBean.setUsername(user.getUsername());
            userBean.setPassword(user.getPassword());
        } catch (IOException e) {
            // Handle JSON parsing exception
            logger.log(Level.SEVERE, "Error parsing JSON: " + e.getMessage(), e);
        }
    }

    private void updateUserInDatabase(String newUsername, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, newUsername);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                logger.log(Level.INFO, "User updated successfully.");
            } else {
                logger.log(Level.WARNING, "User not found or not updated.");
            }
        }
    }
    public List<User> getUserList() {
        return userList;
    }

}
