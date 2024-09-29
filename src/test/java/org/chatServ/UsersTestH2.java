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
        user1 = new Users("111", "aaa", true);
        mess1= new Messages("message1",  time1,  false);
        mess11= new Messages("message11",  time1,  false);

        user2= new Users("222", "bbb", false);
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
        user2.addMessageFromUser(mess2);
        user2.addMessageFromUser(mess22);

        session.beginTransaction();
        session.persist(user1);
        session.persist(user2);
        session.getTransaction().commit();

        String command = "FROM Users WHERE user_name = :user_name";
        Query<Users> query = session.createQuery(command, Users.class);
        query.setParameter("user_name", "111");

        assertEquals("message1", query.getSingleResult().getMessages().get(0).getMessage());
    }





}