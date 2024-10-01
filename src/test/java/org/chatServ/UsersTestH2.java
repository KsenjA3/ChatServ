package org.chatServ;

import org.hibernate.*;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UsersTestH2 {
    private static SessionFactory sessionFactory;
    private static  Session session;

    private static Db db;
    private static Users user1, user2;
    private static Messages mess1,mess11, mess2,mess22 ;

    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector("hibernateTest.cfg.xml");
        sessionFactory = con.sessionFactory;

        db = new Db("hibernateTest.cfg.xml");
        LocalDateTime time1 = LocalDateTime.now();
        user1 = new Users("user1", "aaa", true);
        mess1= new Messages("message1",  time1,  false);
        mess11= new Messages("message11",  time1,  false);

        user2= new Users("user2", "bbb", false);
        LocalDateTime time2 = LocalDateTime.now();
        mess2= new Messages("message2",  time1,  false, time2);
        mess22= new Messages("message22",  time1,  false, time2);
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
    void addMessageFromUser() {
        user1.addMessageFromUser(mess1);
        user1.addMessageFromUser(mess11);
        user2.addMessageToUser(mess1);
        user2.addMessageToUser(mess11);

        user2.addMessageFromUser(mess2);
        user2.addMessageFromUser(mess22);
        user1.addMessageToUser(mess2);
        user1.addMessageToUser(mess22);

        session.beginTransaction();
        session.persist(user1);
        session.persist(user2);
        session.getTransaction().commit();

        String command = "FROM Users WHERE userName = :userName";
        Query<Users> queryU = session.createQuery(command, Users.class);
        queryU.setParameter("userName", "user1");
        Users user_1= queryU.getSingleResult();
        assertEquals("message1", user_1.getMessagesFrom().get(0).getMess());

        command = "FROM Messages WHERE mess = :mess";
        Query<Messages> queryM = session.createQuery(command, Messages.class);
        queryM.setParameter("mess", "message11");
        Messages mess_11 = queryM.getSingleResult();
        assertEquals("user1", mess_11.getFromUser().getUserName());

        int id_user = user_1.getId();
        int id_mess = mess_11.getId();
        System.out.println("id_user= "+id_user);
        System.out.println("id_mess= "+id_mess);
        command = """
                FROM Messages m
                WHERE 
                    m.id != :id
                    AND 
                    m.fromUser.id = :fromUser            
                """;
        queryM = session.createQuery(command, Messages.class);
        queryM.setParameter("id", id_mess);
        queryM.setParameter("fromUser", id_user);
        assertEquals("message1", queryM.getSingleResult().getMess());

    }





}