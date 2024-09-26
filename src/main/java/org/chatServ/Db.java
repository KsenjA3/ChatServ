package org.chatServ;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;

@Log4j2
class Db {
    private final Connector connector;
    protected HashMap<String, String> userListRegistration;
    protected HashMap<String, Boolean> referenceBook ;
    List<Users> users;



    Db (){
        connector = new Connector();
    }

    void createUserDB ( ThreadedEchoServer echoServer){
        userListRegistration=echoServer.getUserListRegistration();
        referenceBook=echoServer.getReferenceBook();

        try (Session session= connector.getSession()) {
            session.beginTransaction();

            Query<Users> query = session.createQuery("FROM Users", Users.class);
            users= query.getResultList();
            users.forEach(u-> {
                userListRegistration.put(u.getUser_name(), u.getPassword());
                referenceBook.put(u.getUser_name(), false);
                u.setIs_online(false);
            });
            log.info("!!!!! User List Registration {}" , userListRegistration);

            session.getTransaction().commit();

            //создание БД Messages

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void addUser (String user_name, String password){
        try (Session session= connector.getSession()) {
            Users u = new Users(user_name, password, false);
            session.beginTransaction();
            session.persist(u);
            session.getTransaction().commit();
        }
    }

    void updateOnline (String user_name, boolean isOnline){
        try (Session session= connector.getSession()) {


            String hql = " FROM Users WHERE user_name = :user_name";
            Query<Users> query = session.createQuery(hql, Users.class);
            query.setParameter("user_name", user_name);
            Users users = query.getSingleResult();
            users.setIs_online(isOnline);

            session.beginTransaction();
            session.persist(users);
            session.getTransaction().commit();
        }
    }
}
