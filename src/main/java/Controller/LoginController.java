package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class LoginController extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private User userBean;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private static Connection connection;
	//private static final Tracer tracer = GlobalTracer.get();

    public User getUserBean() {
        return userBean;
    }

    public void setUserBean(User user) {
        userBean = user;
    }

    public LoginController() {
        super();
        userBean = new User();
        initializeConnection();
        createUser();
    }
    @Trace("AnotherMethod")
    public void initializeConnection() {
        
        String jdbcURL = "jdbc:postgresql://localhost:9001/kys";
        String dbUser = "postgres";
        String dbPassword = "redhat";

        try {
            logger.log(Level.INFO, "Connecting to the database...");
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            if (connection != null && !connection.isClosed()) {
                logger.log(Level.INFO, "Database connection is ready.");
            }

        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to the database", e);
        }
    }

    @Trace("AnotherMethod")    public String createUser() {
		//Span span = tracer.buildSpan("createUser").start();
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            String contentType = request.getContentType();

            if (contentType != null && contentType.startsWith("application/json")) {
                String json = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
                setUserFromJson(json);
                saveUserToDatabase();
                return SUCCESS;
            } else {
                logger.log(Level.WARNING, "Invalid content type. Expected application/json.");
                addActionError("Invalid content type. Expected application/json.");
                return ERROR;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading request body", e);
            return ERROR;
        } finally {
         //   span.finish();
        }
    }
    @Trace("AnotherMethod")
    private void setUserFromJson(String json) {
       // Span span = tracer.buildSpan("setUserFromJson").start();
        try {
            if (json == null || json.isEmpty()) {
                addActionError("Empty or null JSON data.");
                logger.log(Level.WARNING, "Empty or null JSON data.");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(json, User.class);

            if (user.getUsername() != null && !user.getUsername().isEmpty() &&
                    user.getPassword() != null && !user.getPassword().isEmpty()) {
                userBean.setUsername(user.getUsername());
                userBean.setPassword(user.getPassword());
                validate();
            } else {
                addActionError("Username or password is missing in the JSON data.");
                logger.log(Level.WARNING, "Username or password is missing in the JSON data.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error parsing JSON: " + e.getMessage(), e);
        } finally {
           // span.finish();
        }
    }
    @Trace("AnotherMethod")
    private void saveUserToDatabase() {
      //  Span span = tracer.buildSpan("saveUserToDatabase").start();
        try {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, userBean.getUsername());
                preparedStatement.setString(2, userBean.getPassword());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    logger.log(Level.INFO, "User data inserted successfully.");
                } else {
                    logger.log(Level.WARNING, "User data not inserted.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving user data to the database", e);
        } finally {
          //  span.finish();
        }
    }

    public void validate() {
        if (userBean == null) {
            addActionError("Invalid user data.");
            return;
        }

        if (userBean.getUsername() == null || userBean.getUsername().trim().isEmpty()) {
            addFieldError("userBean.username", "Username is required.");
        }

        if (userBean.getPassword() == null || userBean.getPassword().trim().isEmpty()) {
            addFieldError("userBean.password", "Password is required.");
        }
    }
}
