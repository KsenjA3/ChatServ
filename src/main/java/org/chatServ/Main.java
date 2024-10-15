package org.chatServ;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        Connector con = new Connector("hibernate.cfg.xml");
        Db db = new Db("hibernate.cfg.xml");

        db.addUser("u1", "111");
        db.addUser("u2", "222");
        db.addUser("u3", "333");
        Messages mess1,mess11,mess2,mess22,m1,m2,m11,m22;
        mess1= new Messages("message1");
        mess11= new Messages("message11");
        mess2= new Messages("message2");
        mess22= new Messages("message22");

        try(Session session = con.getSession()){
            session.beginTransaction();
            Users user1 =session.get(Users.class, 1);
            Users user2 =session.get(Users.class, 2);
            Users user3 =session.get(Users.class, 3);



            user1.add_oneMessage_to_FromUser(mess1);
            session.persist(user1);

            m1= (Messages) session.get(Messages.class, 1);
            user2.add_oneMessage_to_ToUser(m1);

//            user3.add_oneMessage_to_FromUser(mess11);
//            m11=(Messages)session.get(Messages.class, 2);
//            user2.add_oneMessage_to_ToUser(m11);
//
//            user2.add_oneMessage_to_FromUser(mess2);
//            m2=(Messages)session.get(Messages.class, 3);
//            user1.add_oneMessage_to_ToUser(m2);
//
//            user2.add_oneMessage_to_FromUser(mess22);
//            m22=(Messages)session.get(Messages.class, 4);
//            user3.add_oneMessage_to_ToUser(m22);


            session.persist(user1);
//            session.persist(user2);
//            session.persist(user3);

            session.getTransaction().commit();
        }
    }
}