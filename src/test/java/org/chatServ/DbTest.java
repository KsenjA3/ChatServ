package org.chatServ;

import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    private Query<Users> queryU;
    private Query<Messages> queryM;
    private Query<MessagesUsers> queryMU;

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
        db.addUser("userAddMessage1", "111");
        db.addUser("userAddMessage2", "222");
        db.addUser("userAddMessage3", "333");

        List<String> toUsersList=new ArrayList<String>();
        toUsersList.add("userAddMessage2");
        db.addMessage("content 1 to 2","userAddMessage1",toUsersList);

        toUsersList.add("userAddMessage1");
        db.addMessage("content 3 to list(1,2)","userAddMessage3",toUsersList);

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "userAddMessage2");
        int id_user2 =queryU.getSingleResult().getId();

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "userAddMessage3");
        int id_user3 =queryU.getSingleResult().getId();

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "userAddMessage1");
        int id_user1 =queryU.getSingleResult().getId();

        /*      from        to
                 1           2      content 1 to 2
                 3           1      content 3 to list(1,2)
                 3           2      content 3 to list(1,2)
        */

//        from 1
        commandMessage = """
                FROM  Messages m
                WHERE
                m.fromUser.id = :fromUser
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("fromUser",id_user1);
        assertEquals("content 1 to 2", queryM.getSingleResult().getMess());

//        from 3 to 2
        commandMessage = """               
               FROM MessagesUsers as mu
               WHERE
               mu.user.id= :toUser
               AND
               mu.message.fromUser.id = :fromUser
                """;
        queryMU = session.createQuery(commandMessage, MessagesUsers.class);
        queryMU.setParameter("toUser", id_user2);
        queryMU.setParameter("fromUser",id_user3);
        assertEquals("content 3 to list(1,2)",queryMU.getSingleResult().getMessage().getMess() );

//        count to 2
        commandMessage = """
               FROM MessagesUsers as mu
                WHERE
                mu.user.id= :toUser
                """;
        queryMU = session.createQuery(commandMessage, MessagesUsers.class);
        queryMU.setParameter("toUser", id_user2);
        assertEquals(2,queryMU.getResultList().size() );

//        commandMessage = "FROM Messages";
//        queryM = session.createQuery(commandMessage, Messages.class);
//        List<Messages> mmm = queryM.getResultList();
//        mmm.forEach(m->{
//            System.out.println(m);
//        });
    }

    @Test
    void setGotMessage (){
        db.addUser("userSetGotMessage1", "111");
        db.addUser("userSetGotMessage2", "222");
        db.addUser("userSetGotMessage3", "333");

        List<String> toUsersList=new ArrayList<String>();
        toUsersList.add("userSetGotMessage2");
        db.addMessage("content 3 to 2","userSetGotMessage3",toUsersList);

        toUsersList.add("userSetGotMessage1");
        db.addMessage("content 3 to list(1,2)","userSetGotMessage3",toUsersList);

        //      from        to
        //       3           2      content 3 to 2
        //       3           1      content 3 to list(1,2)
        //       3           2      content 3 to list(1,2)
        db.setGotMessage ( "content 3 to list(1,2)",   "userSetGotMessage3",  "userSetGotMessage2");

        commandMessage = """
               FROM MessagesUsers  mu
               WHERE
               mu.isGot= :isGot
                """;
        queryMU = session.createQuery(commandMessage, MessagesUsers.class);
        queryMU.setParameter("isGot", true);

        assertEquals("content 3 to list(1,2)",queryMU.getSingleResult().getMessage().getMess() );
    }





}