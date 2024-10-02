package org.chatServ;

import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DbEchoHandlerTest {
    private static SessionFactory sessionFactory;
    private Session session;

    private static Db db;
    private static Users user1, user2;
    private static Messages mess1,mess11, mess2,mess22 ;

    @Mock
    static ThreadedEchoServer echoServer;

//    @Mock
    static Socket socket;

    private static ThreadedEchoHandler threadedEchoHandler;

    private HashMap<String, String> userListRegistration;
    private HashMap<String, Boolean> referenceBook ;
    private static String commandUser, commandMessage;
    Query<Users> queryU;
    Query<Messages> queryM;


    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector("hibernateTest.cfg.xml");
        sessionFactory = con.sessionFactory;

        ServerSocket serverSocket = new ServerSocket(8889);
        socket=new Socket("192.168.0.111",8889);
        threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);

        db = new Db("hibernateTest.cfg.xml");
        threadedEchoHandler.setDb(db);

        commandUser = "FROM Users WHERE userName = :userName";
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null) sessionFactory.close();
    }

    @BeforeEach
    void openSession() {
        session = sessionFactory.openSession();
    }

    @AfterEach
    void closeSession() {
        if (session != null) session.close();
    }



    @Test
    void fillTableMessages() {

        userListRegistration= new HashMap<>();
        userListRegistration.put("fillTableMess1", "aaa");
        userListRegistration.put("fillTableMess2", "bbb");
        userListRegistration.put("fillTableMess3", "ccc");

        db.addUser("fillTableMess1", "aaa");
        db.addUser("fillTableMess2", "bbb");
        db.addUser("fillTableMess3", "ccc");

        threadedEchoHandler.fillTableMessages("mess23","fillTableMess2","fillTableMess3");
        threadedEchoHandler.fillTableMessages("mess32","fillTableMess3","fillTableMess2");

        threadedEchoHandler.setEchoServer(echoServer);
        Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);
        threadedEchoHandler.fillTableMessages("messAll","fillTableMess1","to all");

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "fillTableMess2");
        int id_user2 =queryU.getSingleResult().getId();

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "fillTableMess3");
        int id_user3 =queryU.getSingleResult().getId();

        commandMessage = """
                FROM Messages
                WHERE
                fromUser.id = :fromUser
                AND
                toUser.id = :toUser
                """;

        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("fromUser",id_user2);
        queryM.setParameter("toUser",id_user3);
        assertEquals("mess23", queryM.getSingleResult().getMess());

        commandMessage = """
                FROM Messages
                WHERE
                mess = :message
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("message","messAll");
        List<Messages> messagesList = queryM.getResultList();
        System.out.println(messagesList);
        assertEquals(2, messagesList.size());
    }

}