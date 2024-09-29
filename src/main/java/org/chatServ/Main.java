package org.chatServ;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Connector con = new Connector("hibernate.cfg.xml");
        SessionFactory sessionFactory = con.sessionFactory;

        LocalDateTime time1 = LocalDateTime.now();
        Users user1 = new Users("111", "aaa", true);
        Messages mess1= new Messages("message1",  time1,  false);
        Messages mess11= new Messages("message11",  time1,  false);
        user1.addMessageFromUser(mess1);
        user1.addMessageFromUser(mess11);

        Users user2= new Users("222", "bbb", false);
        LocalDateTime time2 = LocalDateTime.now();
        Messages mess2= new Messages("message2",  time1,  false, time2);
        Messages mess22= new Messages("message22",  time1,  false, time2);
        user2.addMessageFromUser(mess2);
        user2.addMessageFromUser(mess22);



        Db db = new Db("hibernate.cfg.xml");
        try (Session session= con.getSession()) {
//            session.beginTransaction();
//            session.persist(user1);
//            session.persist(user2);
//            session.getTransaction().commit();

            String command = "FROM Users WHERE user_name = :user_name";
            Query<Users> query = session.createQuery(command, Users.class);
            query.setParameter("user_name", "111");



            System.out.println(query.getSingleResult().getMessages().get(0).getMessage());

        }




    }
}