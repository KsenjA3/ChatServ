package org.chatServ;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DbTest {
    private static SessionFactory sessionFactory;
    private static Session session;

    private static Db db;
    private static Users user1, user2;
    private static Messages mess1,mess11, mess2,mess22 ;


    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector("hibernateTest.cfg.xml");
        sessionFactory = con.sessionFactory;

        db = new Db("hibernateTest.cfg.xml");
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
    }

    @Test
    void addUser() {
    }

    @Test
    void updateOnline() {
    }

    @Test
    void addMessage() {
    }
}