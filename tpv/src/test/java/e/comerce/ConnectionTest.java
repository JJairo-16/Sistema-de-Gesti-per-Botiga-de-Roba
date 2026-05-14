package e.comerce;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import e.comerce.libs.db.Database;
import e.comerce.libs.db.Pool;

public class ConnectionTest {

    private static Pool pool;
    private static Database db;

    @BeforeAll
    static void setup() {

        pool = Pool.builder()
                .localConnection("tpv_botiga")
                .credentials("root", "48KNfRM2iP7A")
                .build();

        db = new Database(pool);
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

    @AfterAll
    static void teardown() {

        if (pool != null) {
            pool.close();
        }
    }
}