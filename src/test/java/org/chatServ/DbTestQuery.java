package org.chatServ;

import org.hibernate.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbTestQuery {
    private static SessionFactory sessionFactory;
    private Session session;

    Db db;
    HashMap<String, String> listRegistration;
    List<Users> us;


    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector();
        sessionFactory = con.sessionFactory;
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

//        MockitoAnnotations.openMocks(this);
        db = new Db();
        listRegistration= new HashMap<>();

        us= new ArrayList<>(3);
        Users user = new Users("111","aaa", true);
        us.add(user) ;
        user = new Users("222","bbb", false);
        us.add(user) ;
        user = new Users("333","ccc", true);
        us.add(user) ;



//        Mockito.when(connector.getSession()).thenReturn(session);
//        Transaction transaction = null;
//        Mockito.when(session.beginTransaction()).thenReturn(transaction);
//        Mockito.when(session.getTransaction()).thenReturn(transaction);
//
//        Mockito.when(session.createQuery("FROM Users", Users.class)).thenReturn(query);
//        Mockito.when(query.getResultList()).thenReturn(us);

//        db.createUserDB(listRegistration);


        assertEquals("aaa", db.users.get(1).getPassword());
//        assertEquals("aaa", listRegistration.get("111"));
    }

    @Test
    void addUser() {
    }

    @Test
    void updateOnline() {
    }



}