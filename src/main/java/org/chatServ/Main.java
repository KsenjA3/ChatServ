package org.chatServ;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Connector con = new Connector("hibernate.cfg.xml");
        SessionFactory sessionFactory = con.sessionFactory;

        LocalDateTime time1 = LocalDateTime.now();
        Users user1 = new Users("111", "aaa", true);
        Users user2= new Users("222", "bbb", false);

        Messages mess1= new Messages("message1",  time1,  false);
        Messages mess11= new Messages("message11",  time1,  false);
        user1.addMessageFromUser(mess1);
        user1.addMessageFromUser(mess11);
        user2.addMessageToUser(mess1);
        user2.addMessageToUser(mess11);

        LocalDateTime time2 = LocalDateTime.now();
        Messages mess2= new Messages("message2",  time1,  false, time2);
        Messages mess22= new Messages("message22",  time1,  false, time2);
        user2.addMessageFromUser(mess2);
        user2.addMessageFromUser(mess22);
        user1.addMessageToUser(mess2);
        user1.addMessageToUser(mess22);



        Db db = new Db("hibernate.cfg.xml");
        try (Session session= con.getSession()) {
            session.beginTransaction();
            session.persist(user1);
            session.persist(user2);
            session.getTransaction().commit();

            String commandU = "FROM Users" ;
            Query<Users> queryU = session.createQuery(commandU, Users.class);
            List<Users> usersU= queryU.getResultList();
            usersU.forEach(u->{
                System.out.println(u);
            });

            String commandM = "FROM Messages" ;
            Query<Messages> queryM = session.createQuery(commandM, Messages.class);
            List<Messages> usersM= queryM.getResultList();
            usersM.forEach(u->{
                System.out.println(u);
            });

        }




    }
}