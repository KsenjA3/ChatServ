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
    private static Users user1, user2, user3;
    private static Messages mess1,mess11, mess2,mess22 ;

    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector("hibernateTest.cfg.xml");
        sessionFactory = con.sessionFactory;

        db = new Db("hibernateTest.cfg.xml");
        LocalDateTime time1 = LocalDateTime.now();
        user1 = new Users("user1", "aaa", true);
        mess1= new Messages("message1");
        mess11= new Messages("message11");

        user2= new Users("user2", "bbb", false);
        LocalDateTime time2 = LocalDateTime.now();
        mess2= new Messages("message2");
        mess22= new Messages("message22");

        user3= new Users("user3", "ccc", false);
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
        user1.add_oneMessage_to_FromUser(mess1);
        user2.add_oneMessage_to_FromUser(mess2);
        user3.add_oneMessage_to_FromUser(mess11);

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
        queryM.setParameter("mess", "message1");
        Messages mess_1 = queryM.getSingleResult();
        assertEquals("user1", mess_1.getFromUser().getUserName());

        int id_user = user_1.getId();
        command = """
                FROM Messages m
                WHERE m.fromUser.id = :fromUser            
                """;
        queryM = session.createQuery(command, Messages.class);
        queryM.setParameter("fromUser", id_user);
        assertEquals("message1", queryM.getSingleResult().getMess());

    }


    @Test
    void addMessageToUser() {
        session.beginTransaction();
        user1.add_oneMessage_to_FromUser(mess1);
        user2.add_oneMessage_to_ToUser(mess1);

        user3.add_oneMessage_to_FromUser(mess11);
        user2.add_oneMessage_to_ToUser(mess11);

        user2.add_oneMessage_to_FromUser(mess2);
        user1.add_oneMessage_to_ToUser(mess2);

        user2.add_oneMessage_to_FromUser(mess22);
        user3.add_oneMessage_to_ToUser(mess22);


        session.persist(user1);
        session.persist(user2);
        session.persist(user3);
        session.persist(mess1);
        session.persist(mess2);
        session.persist(mess11);
        session.persist(mess22);
        session.getTransaction().commit();


        System.out.println("___________________________________");
        System.out.println(user1.getMessagesUsers());

        /*
               From        To          Message
               user1      user2         mess1
               user3      user2         mess11
               user2      user1         mess2
               user2      user3         mess22
         */

        String command = "FROM Users WHERE userName = :userName";
        Query<Users> queryU = session.createQuery(command, Users.class);
        queryU.setParameter("userName", "user2");
        int id_user2 =queryU.getSingleResult().getId();

        queryU = session.createQuery(command, Users.class);
        queryU.setParameter("userName", "user1");
        int id_user1 =queryU.getSingleResult().getId();

//        command = "FROM Messages WHERE mess = :mess";
//        Query<Messages> queryM = session.createQuery(command, Messages.class);
//        queryM.setParameter("mess", "message11");
//        int id_mess11 = queryM.getSingleResult().getId();


        command = """
                FROM Messages as m
                JOIN m.messagesUsers as messUser
                WHERE                 
                messUser.mu.userId.id= :toUser
                AND
                m.fromUser.id = :fromUser  
                """;
//        Query<Messages> queryM = session.createQuery(command, Messages.class);
//        queryM.setParameter("fromUser", id_user2);
//        queryM.setParameter("toUser", id_user1);
//        assertEquals("message2", queryM.getSingleResult().getMess());
        command = """
                FROM MessagesUsers as messUser
                WHERE                 
                messUser.mu.userId.id= :toUser                 
                """;
        Query<MessagesUsers> queryM = session.createQuery(command, MessagesUsers.class);
        queryM.setParameter("toUser", id_user1);
        assertEquals("message2", queryM.getSingleResult().getMessage().getMess());

    }


}