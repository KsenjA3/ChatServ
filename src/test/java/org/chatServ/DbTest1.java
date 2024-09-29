package org.chatServ;


import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DbTest1 {

    Db db;
    HashMap<String, String> listRegistration;
    List<Users> us;

    @Mock
    Connector connector;

    @Mock
    Session session;

    @Mock
    Query query;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        db = new Db("hibernateTest.cfg.xml");
        listRegistration= new HashMap<>();

        Mockito.when(connector.getSession()).thenReturn(session);

//        Transaction transaction = null;
//        Mockito.when(session.beginTransaction()).thenReturn(transaction);
//        Mockito.when(session.getTransaction()).thenReturn(transaction);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUserDB() {
        us= new ArrayList<>();
        Users user = new Users("111","aaa", true);
        us.add(user) ;
        user = new Users("222","bbb", false);
        us.add(user) ;
        user = new Users("333","ccc", true);
        us.add(user) ;

//        Mockito.when(session.createQuery(ArgumentMatchers.anyString(), any() )).thenReturn(query);
        Mockito.when(session.createQuery(ArgumentMatchers.anyString(), any() )).thenReturn(query);
        Mockito.verify(session.createQuery(ArgumentMatchers.anyString(), any() ));

//        Mockito.when(query.getResultList()).thenReturn(us);
        Mockito.doReturn(us).when(query).getResultList();
        Mockito.verify(query.getResultList());

//        db.createUserDB(listRegistration);

//        assertEquals("aaa", db.users.get(0).getPassword());
    }

    @Test
    void addUser() {
        db.addUser ("addUser", "www");
        assertEquals("aaa", db.users.get(0).getPassword());
    }

    @Test
    void updateOnline() {
    }
}