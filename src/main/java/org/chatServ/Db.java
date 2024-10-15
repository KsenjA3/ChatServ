package org.chatServ;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String commandU;
    private String commandMessage;
    private Query<Users> queryU;
    private Query<Messages> queryM;

    Db (String path){
        connector = new Connector(path);
        commandU = " FROM Users WHERE userName = :user";
    }

    protected void createUserDB ( ThreadedEchoServer echoServer){
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

    protected void addUser (String user_name, String password){
        try (Session session= connector.getSession()) {
            u = new Users(user_name, password, false);

            session.beginTransaction();
            session.persist(u);
            session.getTransaction().commit();
        }
    }

    protected void updateOnline (String userName, boolean is_Online){
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


    protected void addMessage (String mess,  String fromUser,  List<String> toUsersList){
        try (Session session= connector.getSession()) {
            m= new Messages(mess);

            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("user", fromUser);
            u = queryU.getSingleResult();
            m.setFromUser(u);

//            List<Users> toUsers = new ArrayList<>();
            queryU = session.createQuery(commandU, Users.class);
            toUsersList.forEach(toUser->{
                queryU.setParameter("user", toUser);
                u = queryU.getSingleResult();
//                m.add_oneUser_to_Message(u);
//                toUsers.add(u);
            });
//            m.setToUsers(toUsers);

            session.beginTransaction();
            session.persist(m);
            session.getTransaction().commit();
        }
    }
//
//    protected void setGotMessage (String mess,  String fromUser, String toUser){
//        try (Session session= connector.getSession()) {
//            queryU = session.createQuery(commandU, Users.class);
//            queryU.setParameter("user", fromUser);
//            int id_fromUser =queryU.getSingleResult().getId();
//
//            queryU = session.createQuery(commandU, Users.class);
//            queryU.setParameter("user", toUser);
//            int id_toUser =queryU.getSingleResult().getId();
//
//            commandMessage = """
//                SELECT m
//                FROM Messages m
//                JOIN m.toUsers u
//                WHERE u.id = :toUsers
//                AND m.fromUser.id = :fromUser
//                AND m.mess = :mess
//                """;
//            queryM = session.createQuery(commandMessage, Messages.class);
//            queryM.setParameter("toUsers", id_toUser);
//            queryM.setParameter("fromUser",id_fromUser);
//            queryM.setParameter("mess",mess);
//            m = queryM.getSingleResult();
//            m.setGot(true);
//
//            session.beginTransaction();
//            session.persist(m);
//            session.getTransaction().commit();
//        }
//    }
//
//    protected boolean isNewMessagesExist(String toUser) {
//        boolean isMessagesExist = false;
//
//        try (Session session= connector.getSession()) {
//            queryU = session.createQuery(commandU, Users.class);
//            queryU.setParameter("user", toUser);
//            int id_user = queryU.getSingleResult().getId();
//
//            commandMessage = """
//                SELECT m
//                FROM Messages m
//                JOIN m.toUsers u
//                WHERE u.id = :toUsers
//                AND m.isGot= :isGot
//                """;
//
//            queryM = session.createQuery(commandMessage, Messages.class);
//            queryM.setParameter("toUsers", id_user);
//            queryM.setParameter("isGot", false);
//            if(queryM.getResultList().size()>0)
//                isMessagesExist=true;
//        }
//        return isMessagesExist;
//    }
//
//
//    protected String sendRequest(String user, String type, String collocutor, String period ) {
//        String answer="";
//
//        try (Session session= connector.getSession()) {
//             commandU = "FROM Users WHERE userName = :userName";
//            queryU = session.createQuery(commandU, Users.class);
//            queryU.setParameter("userName", user);
//            int id_user = queryU.getSingleResult().getId();
//
//            int id_collocutor=0;
//            if (!collocutor.equals("all")) {
//                queryU = session.createQuery(commandU, Users.class);
//                queryU.setParameter("userName", collocutor);
//                id_collocutor = queryU.getSingleResult().getId();
//            }
//
//
//            StringBuffer command=new StringBuffer();
//            command.append("SELECT m FROM Messages m \nJOIN m.toUsers u\n");
//
//            switch (type){
//                case "all" -> {
//                    if (collocutor.equals("all")){
//                        command.append("WHERE u.id = :user\nOR m.fromUser.id = :user\n");
//                    }
//                    else {
//                        command.append("WHERE (u.id = :user AND m.fromUser.id = :collocutor)\nOR ");
//                        command.append("(u.id = :collocutor AND m.fromUser.id = :user)\n");
//                    }
//                }
//                case "received" -> {
//                    if (collocutor.equals("all"))
//                        command.append("WHERE u.id = :user\n");
//                    else
//                        command.append("WHERE u.id = :user\nAND m.fromUser.id = :collocutor\n");
//                }
//                case "sent" -> {
//                    if (collocutor.equals("all"))
//                        command.append("WHERE m.fromUser.id = :user\n");
//                    else
//                        command.append("WHERE u.id = :collocutor\nAND m.fromUser.id = :user\n");
//                }
//                case "unread" -> {
//                    if (collocutor.equals("all"))
//                        command.append("WHERE u.id = :user \nAND m.isGot= :isGot");
//                    else {
//                        command.append("WHERE u.id = :user \nAND m.fromUser.id = :collocutor\nAND m.isGot= :isGot");
//                    }
//                }
//            }
//
//            switch (period){
//                case "all time" -> {
//
//                }
//                case "for week" -> {
//
//                }
//                case "for month" -> {
//
//                }
//                case "for year" -> {
//
//                }
//            }
//
//            commandMessage=command.toString();
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println(commandMessage);
//
//            queryM = session.createQuery(commandMessage, Messages.class);
//            queryM.setParameter("user", id_user);
//            if (!collocutor.equals("all")) queryM.setParameter("collocutor", id_collocutor);
//            if (type.equals("unread")) queryM.setParameter("isGot", false);
//            List<Messages> messagesList =queryM.getResultList();
//            System.out.println("___________________________________________");
//            System.out.println(messagesList);
//            System.out.println("messagesList= "+messagesList.size());
//
//            queryM = session.createQuery("from Messages", Messages.class);
//            System.out.println(queryM.getResultList());
//            // перебираем users у сообщения
//            // queryM.getSingleResult().getToUsers().size()
//
//
//        }
//        return answer;
//    }



}
