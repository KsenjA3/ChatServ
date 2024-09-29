package org.chatServ;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.*;
import java.util.HashMap;

/** В этой программе реализуется простой сервер, прослушивающий порт
 * 8189 и посылающий обратно клиенту ответ в зависимости от  запроса
 * в своем потоке
 * */

@Getter
@Setter
@Log4j2
public class ThreadedEchoServer {
    static final  int port = 8189;

    /**list registration
     * String - name user
     * String - password
     */
    private HashMap<String, String> userListRegistration;
    /**list user online
     * String - name user
     * Socket - Socket connection
     */
    private HashMap<String, Socket> userListOnline;
    /** reference book
     *String - name user
     *String - marker online
     */
    private HashMap<String, Boolean> referenceBook ;

    ThreadedEchoServer() {
        userListRegistration= new HashMap<>();
        userListOnline=new HashMap<>();
        referenceBook =new HashMap<>();
    }

    public static void main(String[] args)  {

        ThreadedEchoServer echoServer = new ThreadedEchoServer();
        Db db = new Db("hibernate.cfg.xml");
        db.createUserDB (echoServer);

// установить сокет на стороне сервера
        try (ServerSocket s = new ServerSocket(port))
        {
            int i=1;
            while (true){
// ожидает подключение клиента
                Socket incoming = s.accept();
                log.info("Spawning {}" , i);

                Runnable r = new ThreadedEchoHandler(incoming , echoServer);
                Thread t = new Thread(r);
                t .start ();
                i++;

            }
        }

        catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

}

