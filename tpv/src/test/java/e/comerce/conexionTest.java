package e.comerce;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import e.comerce.libs.db.Pool;

public class conexionTest {

    private static Pool pool;

    @BeforeAll
    static void setup() {

        /*
         * Configuración real detectada en db.env:
         *
         * NAME=tpv_botiga
         * USER=root
         * PASSWORD=48KNfRM2iP7A
         */

        pool = Pool.builder()
                .localConnection("tpv_botiga")
                .credentials("root", "48KNfRM2iP7A")
                .poolSize(10, 2)
                .build();
    }

    @Test
    @DisplayName("Debe conectar correctamente usando Hikari Pool")
    void shouldConnectUsingPool() throws Exception {

        try (Connection conn = pool.getDataSource().getConnection()) {

            assertNotNull(conn);
            assertFalse(conn.isClosed());
            assertTrue(conn.isValid(2));

            System.out.println("✅ Conexión válida con HikariCP");
        }
    }

    @Test
    @DisplayName("Debe insertar correctamente un registro en la base de datos")
    void shouldInsertIntoDatabase() throws Exception {

        String sql = """
                    INSERT INTO categories (name)
                    VALUES (?)
                """;

        try (
                Connection conn = pool.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String categoryName = "JUnit_Test_Category";

            stmt.setString(1, categoryName);

            int affectedRows = stmt.executeUpdate();

            assertEquals(1, affectedRows);

            System.out.println("✅ INSERT ejecutado correctamente");
        }
    }

    @AfterAll
    static void teardown() {

        if (pool != null) {
            pool.close();
        }
    }
}