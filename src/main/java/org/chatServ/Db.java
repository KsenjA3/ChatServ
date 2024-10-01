package org.chatServ;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Log4j2
class Db {
    private final Connector connector;
    private HashMap<String, String> userListRegistration;
    private HashMap<String, Boolean> referenceBook ;
    private List<Users> users;
    private Users u;
    private Messages m;
    String commandU;
    Query<Users> queryU;

    Db (String path){
        connector = new Connector(path);
        commandU = " FROM Users WHERE userName = :user";
    }

    void createUserDB ( ThreadedEchoServer echoServer){
        userListRegistration=echoServer.getUserListRegistration();
        referenceBook=echoServer.getReferenceBook();

        try (Session session= connector.getSession()) {
            session.beginTransaction();

            Query<Users> query = session.createQuery("FROM Users", Users.class);
            users= query.getResultList();
            users.forEach(u-> {
                userListRegistration.put(u.getUserName(), u.getPassword());
                referenceBook.put(u.getUserName(), false);
                u.setOnline(false);
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
            u = new Users(user_name, password, false);

            session.beginTransaction();
            session.persist(u);
            session.getTransaction().commit();
        }
    }

    void updateOnline (String userName, boolean is_Online){
        try (Session session= connector.getSession()) {
            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("user", userName);
            u = queryU.getSingleResult();
            u.setOnline(is_Online);

            session.beginTransaction();
            session.persist(u);
            session.getTransaction().commit();
        }
    }


    void addMessage (String mess,  String fromUser, String toUser){
        LocalDateTime time_send = LocalDateTime.now();

        try (Session session= connector.getSession()) {
            m= new Messages(mess,  time_send,  false, null);

            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("user", toUser);
            u = queryU.getSingleResult();
            m.setToUser(u);

            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("user", fromUser);
            u = queryU.getSingleResult();
            m.setFromUser(u);

            session.beginTransaction();
            session.persist(m);
            session.getTransaction().commit();
        }
    }




}
