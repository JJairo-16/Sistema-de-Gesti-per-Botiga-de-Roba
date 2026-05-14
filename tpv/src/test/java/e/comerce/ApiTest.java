package e.comerce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import e.comerce.libs.db.Database;
import e.comerce.libs.db.Pool;
import e.comerce.models.Client;
import e.comerce.services.database.repository.ClientRepository;

class ApiTest {

        private static Pool pool;
        private static Database db;
        private static ClientRepository repository;

        @BeforeAll
        static void setup() {

                pool = Pool.builder()
                                .localConnection("tpv_botiga")
                                .credentials("root", "48KNfRM2iP7A")
                                .build();

                db = new Database(pool);

                repository = new ClientRepository(db);
        }

        @Test
        @DisplayName("API ClientRepository debe insertar, consultar y eliminar clientes")
        void shouldValidateClientRepositoryApi() throws Exception {

                String randomDni = String.format(
                                "%08dA",
                                new Random().nextInt(100000000));

                String randomName = "JUnit_" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

                String randomEmail = randomName.toLowerCase() + "@test.com";

                String randomPhone = String.valueOf(
                                600000000 + new Random().nextInt(99999999));

                Client client = new Client(
                                randomDni,
                                randomName,
                                randomEmail,
                                randomPhone);

                boolean inserted = repository.insert(client);

                assertTrue(inserted);
                assertNotNull(client);

                Client savedClient = repository.findByDni(randomDni);

                assertNotNull(savedClient);

                assertEquals(randomDni, savedClient.dni());
                assertEquals(randomName, savedClient.name());
                assertEquals(randomEmail, savedClient.email());
                assertEquals(randomPhone, savedClient.phone());

                boolean deleted = repository.delete(randomDni);

                assertTrue(deleted);

                Client deletedClient = repository.findByDni(randomDni);

                assertNull(deletedClient);

                System.out.println("✅ API ClientRepository validada correctamente");
        }

        @AfterAll
        static void teardown() {

                if (pool != null) {
                        pool.close();
                }
        }
}
