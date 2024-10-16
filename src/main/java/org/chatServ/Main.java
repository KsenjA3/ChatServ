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
//        Connector con = new Connector("hibernate.cfg.xml");
        Db db = new Db("hibernate.cfg.xml");
        db.setGotMessage("from 2 to 1,3","222","333");

//        db.addUser("444", "444");
//        db.addUser("222", "222");
//        db.addUser("333", "333");
//        Messages mess1,mess11,mess2,mess22,m1,m2,m11,m22;
//        mess1= new Messages("message1");
//        mess11= new Messages("message11");
//        mess2= new Messages("message2");
//        mess22= new Messages("message22");

//        try(Session session = con.getSession()){
//            session.beginTransaction();
//            Users user1 =session.get(Users.class, 1);
//            Users user2 =session.get(Users.class, 2);
//            Users user3 =session.get(Users.class, 3);
//
//
//
//            user1.add_oneMessage_to_FromUser(mess1);
//            user3.add_oneMessage_to_ToUser(mess1);
//            session.persist(user3);
//            session.persist(user1);
//
//
//            user2.add_oneMessage_to_FromUser(mess2);
//            user1.add_oneMessage_to_ToUser(mess2);
//            session.persist(user2);
//            session.persist(user1);
//
//            session.persist(user3);

//            session.getTransaction().commit();
//        }
    }
}