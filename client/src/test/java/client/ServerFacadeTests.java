package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {
    private static String serverUrl;
    private static Server server;
    private ServerFacade sv;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }
    @BeforeEach
    public void setUp(){
        sv = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerTest(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            System.out.println(authData.authToken());
            assert authData.authToken() != null;
            sv.logout(authData.authToken());

        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }

    }

    @Test
    public void loginTest(){
        try {
            sv.clear();
            AuthData authData = sv.register("urmom", "ishot", "doIlooklike@gmail.com");
            sv.logout(authData.authToken());
            AuthData authData2 = sv.login("urmom", "ishot");
            System.out.println(authData.authToken());
            assert authData2.authToken() != null;

        }catch(Exception ex){
            ex.getMessage();
            Assertions.fail();
        }

    }

}
