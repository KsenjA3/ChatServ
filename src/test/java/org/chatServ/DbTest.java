package org.chatServ;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DbTest {
    private static SessionFactory sessionFactory;
    private  Session session;

    private static Db db;
    private static Users user1, user2;
    private static Messages mess1,mess11, mess2,mess22 ;

    @Mock
    ThreadedEchoServer echoServer;

    private HashMap<String, String> userListRegistration;
    private HashMap<String, Boolean> referenceBook ;
    private static String commandUser, commandMessage;
    Query<Users> queryU;
    Query<Messages> queryM;


    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector("hibernateTest.cfg.xml");
        sessionFactory = con.sessionFactory;

        db = new Db("hibernateTest.cfg.xml");
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
    void createUserDB() {
        user1 = new Users("createUserDB1", "aaa", true);
        user2= new Users("createUserDB2", "bbb", false);

        session.beginTransaction();
        session.persist(user1);
        session.persist(user2);
        session.getTransaction().commit();

        userListRegistration=new HashMap<>();
        referenceBook=new HashMap<>();
        Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);
        Mockito.when(echoServer.getReferenceBook()).thenReturn(referenceBook);

        db.createUserDB(echoServer);
        assertEquals("aaa", userListRegistration.get("createUserDB1"));
    }

    @Test
    void addUser() {
        db.addUser("addUser", "ccc");
        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "addUser");
        assertEquals("ccc", queryU.getSingleResult().getPassword());
    }

    @Test
    void updateOnline() {
        db.addUser("updateOnline", "ddd");
        db.updateOnline("updateOnline", true);
        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "updateOnline");

        assertEquals(true, queryU.getSingleResult().isOnline());

    }

    @Test
    void addMessage() {
        db.addUser("addMessage1", "111");
        db.addUser("addMessage2", "222");
        db.addUser("addMessage3", "111");
        db.addMessage("content 1","addMessage1","addMessage2");
        db.addMessage("content 2","addMessage1","addMessage3");
        db.addMessage("content 3","addMessage2","addMessage3");
        db.addMessage("content 4","addMessage2","addMessage1");
        db.addMessage("content 5","addMessage3","addMessage1");
        db.addMessage("content 6","addMessage3","addMessage2");

//        commandMessage = "FROM Messages";
//        queryM = session.createQuery(commandMessage, Messages.class);
//        List<Messages> mmm = queryM.getResultList();
//        mmm.forEach(m->{
//            System.out.println(m);
//        });

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "addMessage2");
        int id_userFrom =queryU.getSingleResult().getId();

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "addMessage3");
        int id_userTo =queryU.getSingleResult().getId();

        commandMessage = """ 
                FROM Messages 
                WHERE 
                fromUser.id = :fromUser
                AND
                toUser.id = :toUser
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("fromUser",id_userFrom);
        queryM.setParameter("toUser",id_userTo);

        assertEquals("content 3", queryM.getSingleResult().getMess());
    }
}