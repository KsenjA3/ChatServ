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
        user1 = new Users("userFrom1", "aaa", true);
        user2= new Users("userFrom2", "bbb", false);
        user3= new Users("userFrom3", "ccc", false);
        mess1= new Messages("messageFrom1");
        mess11= new Messages("messageFrom11");
        mess2= new Messages("messageFrom2");
        mess22= new Messages("messageFrom22");

        user1.add_oneMessage_to_FromUser(mess1);
        user2.add_oneMessage_to_FromUser(mess2);
        user3.add_oneMessage_to_FromUser(mess11);

        session.beginTransaction();
        session.persist(user1);
        session.persist(user2);
        session.getTransaction().commit();

        String command = "FROM Users WHERE userName = :userName";
        Query<Users> queryU = session.createQuery(command, Users.class);
        queryU.setParameter("userName", "userFrom1");
        Users user_1= queryU.getSingleResult();
        assertEquals("messageFrom1", user_1.getMessagesFrom().get(0).getMess());

        command = "FROM Messages WHERE mess = :mess";
        Query<Messages> queryM = session.createQuery(command, Messages.class);
        queryM.setParameter("mess", "messageFrom1");
        Messages mess_1 = queryM.getSingleResult();
        assertEquals("userFrom1", mess_1.getFromUser().getUserName());

        int id_user = user_1.getId();
        command = """
                FROM Messages m
                WHERE m.fromUser.id = :fromUser            
                """;
        queryM = session.createQuery(command, Messages.class);
        queryM.setParameter("fromUser", id_user);
        assertEquals("messageFrom1", queryM.getSingleResult().getMess());

    }

    @Test
    void addMessageToUser() {
        user1 = new Users("user1", "aaa", true);
        user2= new Users("user2", "bbb", false);
        user3= new Users("user3", "ccc", false);
        mess1= new Messages("message1");
        mess11= new Messages("message11");
        mess2= new Messages("message2");
        mess22= new Messages("message22");


        session.beginTransaction();

        user1.add_oneMessage_to_FromUser(mess1);
        user2.add_oneMessage_to_ToUser(mess1);

        user1.add_oneMessage_to_FromUser(mess11);
        user3.add_oneMessage_to_ToUser(mess11);

        user2.add_oneMessage_to_FromUser(mess2);
        user1.add_oneMessage_to_ToUser(mess2);

        user2.add_oneMessage_to_FromUser(mess22);
        user3.add_oneMessage_to_ToUser(mess22);

        session.persist(user2);
        session.persist(user3);
        session.persist(user1);

        session.getTransaction().commit();
        /*
               From        To          Message
               user1      user2         mess1
               user1      user3         mess11
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

        queryU = session.createQuery(command, Users.class);
        queryU.setParameter("userName", "user3");
        int id_user3 =queryU.getSingleResult().getId();

        command = """
                FROM MessagesUsers as mu
                WHERE
                mu.user.id= :toUser
                """;
        Query<MessagesUsers> queryMU = session.createQuery(command, MessagesUsers.class);
        queryMU.setParameter("toUser", id_user1);
        assertEquals("message2", queryMU.getSingleResult().getMessage().getMess());

        command = """        
                FROM MessagesUsers as mu
                WHERE
                mu.user.id= :toUser
                AND
                mu.message.fromUser.id = :fromUser 
                """;
        queryMU = session.createQuery(command, MessagesUsers.class);
        queryMU.setParameter("fromUser", id_user2);
        queryMU.setParameter("toUser", id_user3);
        assertEquals("message22", queryMU.getSingleResult().getMessage().getMess());

        queryMU = session.createQuery(command, MessagesUsers.class);
        queryMU.setParameter("fromUser", id_user1);
        queryMU.setParameter("toUser", id_user2);
        assertEquals("message1", queryMU.getSingleResult().getMessage().getMess());

    }

}