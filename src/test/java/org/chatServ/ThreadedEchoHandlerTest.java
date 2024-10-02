package org.chatServ;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ThreadedEchoHandlerTest {
    @Mock
    ThreadedEchoServer echoServer;

    @Mock
    Db db;

     private static HashMap<String, Socket> userListOnline;
     private static HashMap<String, String> userListRegistration;
     private ThreadedEchoHandler threadedEchoHandler;
     private Socket clientsocket, socket;
     private ServerSocket serverSocket;
     private BufferedReader br;
     private PrintWriter out;
     private String response;

    @BeforeAll
    static void beforeAll() {
        userListRegistration=new HashMap<>();
        userListRegistration.put("111","aaa");
        userListRegistration.put("222","bbb");

        userListOnline=new HashMap<>();
    }

    @BeforeEach
    void setUp() {
        try {
           serverSocket = new ServerSocket(8888);
           serverSocket.setSoTimeout(1000000);
           response="dawn";

            clientsocket=new Socket("192.168.0.111",8888);

            clientsocket.setSoTimeout(1000);
            InputStream inStream = clientsocket.getInputStream();
            OutputStream outStream = clientsocket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(inStream));
            out = new PrintWriter(outStream, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @AfterEach
    void tearDown()  {
        userListOnline.remove("111");
//        socket.close();
        serverSocket.close();
        br.close();
        out.close();
        clientsocket.close();
    }


    @SneakyThrows
    @Test
    void inAccountNoUser() {
        Thread thread = new Thread() {

            public void run (){
                try{
                    try {
                    socket=serverSocket.accept();
                    socket.setSoTimeout(1000000);
                    threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);
                    threadedEchoHandler.setDb(db);

                    Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);

                    threadedEchoHandler.inAccount("333", "aaa");
                    }finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        Thread.sleep(100);
        if (br.ready())
            response =br.readLine();
        assertEquals("NoUser", response);
    }

    @SneakyThrows
    @Test
    void inAccountNoPassword() {
        Thread thread = new Thread() {
            public void run (){
                try{
                    try {
                    socket=serverSocket.accept();
                    socket.setSoTimeout(1000000);
                    threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);
                    threadedEchoHandler.setDb(db);

                    Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);

                    threadedEchoHandler.inAccount("111", "ppp");
                    }finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        Thread.sleep(100);
        if (br.ready())
            response =br.readLine();
        assertEquals("NoPassword", response);
    }

    @SneakyThrows
    @Test
    void inAccountBUSY() {
        Thread thread = new Thread() {
            public void run (){
                try{
                    try {
                    socket=serverSocket.accept();
                    socket.setSoTimeout(1000000);
                    threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);
                    threadedEchoHandler.setDb(db);

                    Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);
                    Mockito.when(echoServer.getUserListOnline()).thenReturn(userListOnline);

                    userListOnline.put("111",socket);
                    threadedEchoHandler.inAccount("111", "aaa");
                    }finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        Thread.sleep(100);
        if (br.ready())
            response =br.readLine();
        assertEquals("BUSY", response);
    }

    @SneakyThrows
    @Test
    void inAccountOK() {
        Thread thread = new Thread() {
            public void run (){
                try{
                    try {
                        socket=serverSocket.accept();
                        socket.setSoTimeout(1000000);
                        threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);
                        threadedEchoHandler.setDb(db);

                        Mockito.doNothing().when(db).updateOnline(Mockito.anyString(), Mockito.anyBoolean());
                        Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);
                        Mockito.when(echoServer.getUserListOnline()).thenReturn(userListOnline);

                        userListOnline.put("111", socket);
                        threadedEchoHandler.inAccount("222", "bbb");
                    }finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        Thread.sleep(100);
        if (br.ready())
            response =br.readLine();
        assertEquals("OK", response);
    }

    @SneakyThrows
    @Test
    void registrationAccountBUSY() {
        Thread thread = new Thread() {
            public void run (){
                try{
                    try {
                    socket=serverSocket.accept();
                    socket.setSoTimeout(1000000);
                    threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);
                    threadedEchoHandler.setDb(db);

                    Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);

                    threadedEchoHandler.registrationAccount("111", "aaa");
                    }finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        Thread.sleep(100);
        if (br.ready())
            response =br.readLine();

        assertEquals("BUSY", response);
    }

    @SneakyThrows
    @Test
    void registrationAccountOK() {
        Thread thread = new Thread() {
            public void run (){
                try{
                    try {
                    socket=serverSocket.accept();
                    socket.setSoTimeout(1000000);
                    threadedEchoHandler= new ThreadedEchoHandler(socket , echoServer);
                    threadedEchoHandler.setDb(db);

                    Mockito.when(echoServer.getUserListRegistration()).thenReturn(userListRegistration);

                    threadedEchoHandler.registrationAccount("333", "ccc");
                    }finally {
                        socket.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        thread.start();

        Thread.sleep(100);
        if (br.ready())
            response =br.readLine();

        assertEquals("OK", response);
    }







//
//    @Test
//    void send_referenceBook() {
//    }
//
//    @Test
//    void send_message() {
//    }
}