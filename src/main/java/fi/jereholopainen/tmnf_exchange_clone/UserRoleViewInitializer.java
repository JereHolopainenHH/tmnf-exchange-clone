package fi.jereholopainen.tmnf_exchange_clone;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class UserRoleViewInitializer {

    private final DataSource dataSource;

    public UserRoleViewInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        // Connect to the database and create the view between app_user and role tables
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {

            // SQL to drop the view if it exists
            String dropSql = "DROP VIEW IF EXISTS user_roles_view";

            // Execute the drop SQL
            statement.execute(dropSql);

            // SQL to create the view
            String createSql = """
                        CREATE VIEW user_roles_view AS
                        SELECT au.username, r.name AS role_name
                        FROM app_user au
                        JOIN user_role ur ON au.id = ur.user_id
                        JOIN role r ON ur.role_id = r.id;
                    """;

            // Execute the create SQL
            statement.execute(createSql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
