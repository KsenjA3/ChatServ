package org.chatServ;

import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DbEchoHandlerTest {
    private static SessionFactory sessionFactory;
    private Session session;

    private static Db db;
    private static Users user1, user2;
    private static Messages mess1,mess11, mess2,mess22 ;

    @Mock
    static ThreadedEchoServer echoServer;

//    @Mock
    static Socket socket;

    private static ThreadedEchoHandler threadedEchoHandler;

    private HashMap<String, String> userListRegistration;
    private HashMap<String, Boolean> referenceBook ;
    private static String commandUser, commandMessage;
    Query<Users> queryU;
    Query<Messages> queryM;


    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        Connector con = new Connector("hibernateTest.cfg.xml");
        sessionFactory = con.sessionFactory;

        ServerSocket serverSocket = new ServerSocket(8889);
        socket=new Socket("192.168.0.111",8889);
        threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);

        db = new Db("hibernateTest.cfg.xml");
        threadedEchoHandler.setDb(db);

        commandUser = "FROM Users WHERE userName = :userName";
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
    void fillTableMessages() {
        userListRegistration = new HashMap<>();
        userListRegistration.put("userFillTableMess1", "aaa");
        userListRegistration.put("userFillTableMess2", "bbb");
        userListRegistration.put("userFillTableMess3", "ccc");

        db.addUser("userFillTableMess1", "aaa");
        db.addUser("userFillTableMess2", "bbb");
        db.addUser("userFillTableMess3", "ccc");

        List<String> receiversListNewMess;
        threadedEchoHandler.setEchoServer(echoServer);
        Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);

        receiversListNewMess=threadedEchoHandler.fillTableMessages("mess_from3_to2", "userFillTableMess3", "<html>userFillTableMess2</html>");
        assertEquals("userFillTableMess2",receiversListNewMess.get(0));

        receiversListNewMess=threadedEchoHandler.fillTableMessages("mess_from2_to3", "userFillTableMess2", "<html>userFillTableMess3</html>");
        assertEquals("userFillTableMess3",receiversListNewMess.get(0));

        receiversListNewMess=threadedEchoHandler.fillTableMessages("mess_from3_to_list(1,2)", "userFillTableMess3", "<html>userFillTableMess2<br>userFillTableMess1</html>");
        assertTrue(receiversListNewMess.contains("userFillTableMess2"));

        receiversListNewMess=threadedEchoHandler.fillTableMessages("messAll_from1", "userFillTableMess1", "<html>to all</html>");
        assertEquals(2,receiversListNewMess.size());


        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "userFillTableMess2");
        int id_user2 = queryU.getSingleResult().getId();

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "userFillTableMess3");
        int id_user3 = queryU.getSingleResult().getId();

        queryU = session.createQuery(commandUser, Users.class);
        queryU.setParameter("userName", "userFillTableMess1");
        int id_user1 = queryU.getSingleResult().getId();

/**
        from        to      message
         3           2      mess_from3_to2
         2           3      mess_from2_to3
         3           1      mess_from3_to_list(1,2)
         3           2      mess_from3_to_list(1,2)
         1           2      messAll_from1
         1           3      messAll_from1
**/

//        mess to 1 = mess_from3_to_list(1,2)
        commandMessage = """
                SELECT m
                FROM Messages m
                JOIN m.toUsers u 
                WHERE u.id = :toUsers 
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("toUsers", id_user1);
        assertEquals("mess_from3_to_list(1,2)", queryM.getSingleResult().getMess());

        // count  mess to 2 = 2
        commandMessage = """
                SELECT m
                FROM Messages m
                JOIN m.toUsers u 
                WHERE u.id = :toUsers 
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("toUsers", id_user2);
        assertEquals(3, queryM.getResultList().size());


//        mess from 3 to 2 = mess_from3_to_list(1,2)
        commandMessage = """
                 SELECT m
                FROM Messages m
                JOIN m.toUsers u 
                WHERE u.id = :toUsers 
                AND
                fromUser.id = :fromUser
                                
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("toUsers", id_user2);
        queryM.setParameter("fromUser", id_user3);
        assertEquals(2, queryM.getResultList().size());


//        mess from 3 and to 3 = count(3)
        commandMessage = """
                SELECT m
                FROM Messages m
                JOIN m.toUsers u 
                WHERE u.id = :toUsers 
                OR
                m.fromUser.id = :fromUser                                
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("toUsers", id_user3);
        queryM.setParameter("fromUser", id_user3);
        assertEquals(4, queryM.getResultList().size());

/**
        from        to      message                         id_mess
         3           2      mess_from3_to2                    1
         2           3      mess_from2_to3                    2
        |3           1      mess_from3_to_list(1,2)           3
        |3           2      mess_from3_to_list(1,2)           3
         1           2      messAll_from1                     4
         1           3      messAll_from1                     4

        [Messages{id=1, mess='mess_from2_to3', timeSend=2024-10-11T12:19:47.769825, isGot=false, timeReceive=null,
        fromUser=Users{id=2, userName='userFillTableMess2', password='bbb', isOnline=false}},

        Messages{id=2, mess='mess_from3_to_list(1,2)', timeSend=2024-10-11T12:19:47.798816, isGot=false, timeReceive=null,
        fromUser=Users{id=3, userName='userFillTableMess3', password='ccc', isOnline=false}},

        Messages{id=3, mess='messAll_from1', timeSend=2024-10-11T12:19:47.840826, isGot=false, timeReceive=null,
        fromUser=Users{id=1, userName='userFillTableMess1', password='aaa', isOnline=false}}]
**/

//        mess from 3  = count(2)
        commandMessage = """
                SELECT m
                FROM Messages m               
                WHERE 
                m.fromUser.id = :fromUser                                              
                """;
        queryM = session.createQuery(commandMessage, Messages.class);
        queryM.setParameter("fromUser", id_user3);
        assertEquals(2, queryM.getResultList().size());
        assertEquals(3, queryM.getResultList().get(0).getToUsers().size()+
                                    queryM.getResultList().get(1).getToUsers().size() );
    }

    @Test
    void sendRequest() {
        userListRegistration = new HashMap<>();
        userListRegistration.put("userSendRequest1", "aaa");
        userListRegistration.put("userSendRequest2", "bbb");
        userListRegistration.put("userSendRequest3", "ccc");

        db.addUser("userSendRequest1", "aaa");
        db.addUser("userSendRequest2", "bbb");
        db.addUser("userSendRequest3", "ccc");

        List<String> receiversListNewMess;
        threadedEchoHandler.setEchoServer(echoServer);
        Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);

        threadedEchoHandler.fillTableMessages("mess_from3_to2", "userSendRequest3",
                "<html>userSendRequest2</html>");
        threadedEchoHandler.fillTableMessages("mess_from2_to3", "userSendRequest2",
                "<html>userSendRequest3</html>");
        threadedEchoHandler.fillTableMessages("mess_from3_to_list(1,2)", "userSendRequest3",
                "<html>userSendRequest2<br>userSendRequest1</html>");
        threadedEchoHandler.fillTableMessages("messAll_from1", "userSendRequest1",
                "<html>to all</html>");

        db.setGotMessage("mess_from3_to2","userSendRequest3","userSendRequest2");
        db.setGotMessage("mess_from2_to3","userSendRequest2","userSendRequest3");
        db.setGotMessage("mess_from3_to_list(1,2)","userSendRequest3","userSendRequest1");
        db.setGotMessage("messAll_from1","userSendRequest1","userSendRequest3");

        /**
         from        to      message                         id_mess        isGot
         3           2      mess_from3_to2                    1                1
         2           3      mess_from2_to3                    2                1
         |3          1      mess_from3_to_list(1,2)           3                1
         |3          2      mess_from3_to_list(1,2)           3                0
         1           2      messAll_from1                     4                0
         1           3      messAll_from1                     4                1
         **/

        // mess_from3_to2  -  mess_from3_to_list(1,2)
//        db.sendRequest( "userSendRequest2",  "received",  "userSendRequest3",  "all time" );


        // mess_from3_to2  -  mess_from3_to_list(1,2)  -  messAll_from1
//        db.sendRequest( "userSendRequest2",  "received",  "all",  "all time" );

        // mess_from3_to2  -  mess_from3_to_list(1,2)
//        db.sendRequest( "userSendRequest3",  "sent",  "userSendRequest2",  "all time" );


        // mess_from3_to2  -  mess_from3_to_list(1,2)  !list-2
//        db.sendRequest( "userSendRequest3",  "sent",  "all",  "all time" );

        // mess_from3_to2  -  mess_from2_to3  -  mess_from3_to_list(1,2)
//        db.sendRequest( "userSendRequest3",  "all",  "userSendRequest2",  "all time" );

        // mess_from3_to2  -  mess_from2_to3  -  mess_from3_to_list(1,2)  -  messAll_from1
//        db.sendRequest( "userSendRequest3",  "all",  "all",  "all time" );

        // mess_from3_to_list(1,2)
//        db.sendRequest( "userSendRequest2",  "unread",  "userSendRequest3",  "all time" );

    }

 }