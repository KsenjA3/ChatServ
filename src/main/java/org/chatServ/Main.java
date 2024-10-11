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
//        Db db = new Db("hibernate.cfg.xml");

//        db.addUser("u1", "111");
//        db.addUser("u2", "222");
//        db.addUser("u3", "333");
//
//        List<String> toUsersList=new ArrayList<String>();
//        toUsersList.add("u2");
//        db.addMessage("content 1 to 2","u1",toUsersList);
//
//        toUsersList.add("u1");
//        db.addMessage("content 3 to list(1,2)","u3",toUsersList);

//        try(Session session = con.getSession()){
//
//            session.beginTransaction();
//            Messages mess = session.get(Messages.class,12);
//            Users user = session.get(Users.class, 4);
//            System.out.println(mess);
//            session.delete(user);
//            session.getTransaction().commit();


        String str="userFillTableMess2<br>userFillTableMess1";

        String [] strMass = StringUtils.split(str,"<br>");
//                str.split("<br>");
        List<String> strList = Arrays.stream(strMass).toList();
        System.out.println(strList);

//        }
    }
}