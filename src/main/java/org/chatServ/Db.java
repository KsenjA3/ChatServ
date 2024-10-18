package org.chatServ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
class Db {
    private final Connector connector;
    private HashMap<String, String> userListRegistration, correspondence;
    private HashMap<String, Boolean> referenceBook ;
    private List<Users> users;
    private Users u;
    private Messages m;
    private String commandU;
    private String commandMessage;
    private Query<Users> queryU;
    private Query<Messages> queryM;
    private Query<MessagesUsers> queryMU;

    Db (String path){
        connector = new Connector(path);
        commandU = " FROM Users WHERE userName = :userName";
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
            queryU.setParameter("userName", userName);
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
            queryU.setParameter("userName", fromUser);
            u = queryU.getSingleResult();

            session.beginTransaction();
            u.add_oneMessage_to_FromUser(m);
            session.persist(u);

            queryU = session.createQuery(commandU, Users.class);
            toUsersList.forEach(toUser->{
                queryU.setParameter("userName", toUser);
                u = queryU.getSingleResult();
                u.add_oneMessage_to_ToUser(m);
                session.persist(u);
            });
            session.getTransaction().commit();
        }
    }

    protected void setGotMessage (String mess,  String fromUser, String toUser){
        try (Session session= connector.getSession()) {
            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("userName", fromUser);
            int id_fromUser =queryU.getSingleResult().getId();

            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("userName", toUser);
            int id_toUser =queryU.getSingleResult().getId();

            commandMessage = """
               FROM MessagesUsers as mu
               WHERE
               mu.user.id= :toUser
               AND
               mu.message.fromUser.id = :fromUser
               AND 
               mu.message.mess = :mess
                """;
            queryMU = session.createQuery(commandMessage, MessagesUsers.class);
            queryMU.setParameter("toUser", id_toUser);
            queryMU.setParameter("fromUser",id_fromUser);
            queryMU.setParameter("mess",mess);

            MessagesUsers mu = queryMU.getSingleResult();
            mu.setGot(true);

            session.beginTransaction();
            session.persist(mu);
            session.getTransaction().commit();
        }
    }

    protected boolean isNewMessagesExist(String toUser) {
        boolean isMessagesExist = false;

        try (Session session= connector.getSession()) {
            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("userName", toUser);
            int id_user = queryU.getSingleResult().getId();

            commandMessage = """
                FROM MessagesUsers mu
                WHERE
                mu.user.id= :toUser
                AND 
                mu.isGot= :isGot
                """;
            queryMU = session.createQuery(commandMessage, MessagesUsers.class);
            queryMU.setParameter("toUser", id_user);
            queryMU.setParameter("isGot", false);

            if(queryMU.getResultList().size()>0)
                isMessagesExist=true;
        }
        return isMessagesExist;
    }

    protected String sendRequest(String user, String type, String collocutor, String period ) {
        String answer;

        try (Session session= connector.getSession()) {

            commandU = "FROM Users WHERE userName = :userName";
            queryU = session.createQuery(commandU, Users.class);
            queryU.setParameter("userName", user);
            int id_user = queryU.getSingleResult().getId();

            int id_collocutor=0;
            if (!collocutor.equals("all")) {
                queryU = session.createQuery(commandU, Users.class);
                queryU.setParameter("userName", collocutor);
                id_collocutor = queryU.getSingleResult().getId();
            }

            StringBuffer command=new StringBuffer();
            command.append("FROM MessagesUsers as mu\nWHERE\n");

            switch (type){
                case "all" -> {
                    if (collocutor.equals("all")){
                        command.append("mu.user.id = :user\nOR mu.message.fromUser.id = :user\n");
                    }
                    else {
                        command.append("(mu.user.id = :user AND mu.message.fromUser.id = :collocutor)\nOR "
                        +"(mu.user.id = :collocutor AND mu.message.fromUser.id = :user)\n");
                    }
                }
                case "received" -> {
                    if (collocutor.equals("all"))
                        command.append("mu.user.id = :user\n");
                    else
                        command.append("mu.user.id = :user\nAND mu.message.fromUser.id = :collocutor\n");
                }
                case "sent" -> {
                    if (collocutor.equals("all"))
                        command.append("mu.message.fromUser.id = :user\n");
                    else
                        command.append("mu.user.id = :collocutor\nAND mu.message.fromUser.id = :user\n");
                }
                case "unread" -> {
                    if (collocutor.equals("all"))
                        command.append("mu.user.id = :user \nAND mu.isGot= :isGot\n");
                    else {
                        command.append("mu.user.id = :user \nAND mu.message.fromUser.id = :collocutor\nAND mu.isGot= :isGot\n");
                    }
                }
            }

            LocalDateTime sinceTime=LocalDateTime.MIN;
            LocalDateTime nowTime=LocalDateTime.now();
            if (!period.equals("all time"))     {
                command.append("AND mu.message.timeSend >= :sinceTime \n");
                switch (period){
                    case "for week" -> {
                        sinceTime=nowTime.minusWeeks(1);
//                        sinceTime=nowTime.minusMinutes(2);
                    }
                    case "for month" -> {
                        sinceTime=nowTime.minusMonths(1);
//                        sinceTime=nowTime.minusHours(1);
                    }
                    case "for year" -> {
                        sinceTime=nowTime.minusYears(1);
//                        sinceTime=nowTime.minusDays(5);
                    }
                }
            }

            // сортировка по timeSend
//            command.append("ORDER BY mu.message.timeSend");

            commandMessage=command.toString();
            queryMU = session.createQuery(commandMessage, MessagesUsers.class);
            queryMU.setParameter("user", id_user);
            if (!period.equals("all time"))     queryMU.setParameter("sinceTime", sinceTime);
            if (!collocutor.equals("all"))      queryMU.setParameter("collocutor", id_collocutor);
            if (type.equals("unread"))          queryMU.setParameter("isGot", false);
            List<MessagesUsers> messagesUsersList =queryMU.getResultList();

            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(commandMessage);
            System.out.println("messagesList= "+messagesUsersList.size());

/*            перебираем queryMU.list() и заполняем HashMap, где формат ключа:
                  -FROM userFrom :timeSend (familiarized timeReceive)
                  -TO userTo :timeSend (familiarized timeReceive)
*/
            correspondence=new HashMap<>();
            String pattern = "dd'.'MM'.'yyyy' at 'HH'.'mm'.'ss";

            messagesUsersList.forEach( mu-> {
                String interlocutor="<html>";
                if(user.equals(mu.getUser().getUserName())) {
                    interlocutor = interlocutor + String.format("on %s-FROM %s:",
                        mu.getMessage().getTimeSend().format(DateTimeFormatter.ofPattern(pattern)),
                        mu.getMessage().getFromUser().getUserName());
                }
                else if(user.equals(mu.getMessage().getFromUser().getUserName()))
                    interlocutor=interlocutor+String.format("on %s-TO %s:",
                        mu.getMessage().getTimeSend().format(DateTimeFormatter.ofPattern(pattern)),
                        mu.getUser().getUserName());

                if (mu.isGot())
                    interlocutor=interlocutor+String.format("<br>(familiarized on %s)",
                        mu.getTimeReceive().format(DateTimeFormatter.ofPattern(pattern)));

                interlocutor=interlocutor+"</html>";
                correspondence.put(interlocutor,mu.getMessage().getMess()) ;
            });

            System.out.println("___________________________________________");
            System.out.println("correspondence_to_JSON"+correspondence);

            // преобразуем Map в строку JSON для пересылки
            answer = correspondence_to_JSON ();
        }
        return answer;
    }

    String correspondence_to_JSON (){
        String correspondenceJson=null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            correspondenceJson = mapper.writeValueAsString(correspondence);
            log.info("{} - correspondence " ,correspondenceJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return correspondenceJson;
    }

}
