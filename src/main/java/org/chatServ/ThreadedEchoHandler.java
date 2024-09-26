package org.chatServ;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
//import org.json.JSObject;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.*;
import java.util.*;

@Log4j2

class ThreadedEchoHandler implements Runnable{
    private Socket incoming;
    private ThreadedEchoServer echoServer;

    private PrintWriter outNet;
    private boolean done;

    ThreadedEchoHandler(Socket s, ThreadedEchoServer echoServ){
        incoming=s;
        echoServer=echoServ;
    }
    public void run() {
        String line, command, user="", message="", receiver="";
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();
                BufferedReader brNet = new BufferedReader(new InputStreamReader(inStream));
                outNet = new PrintWriter(outStream, true);

                done= true;
                while (done) {

                    /** Формирует полный запрос от клиента:
                     * command, user, message.
                     * При неудаче начинает сначала.
                     */
                    while (brNet.ready()) {
                        line=brNet.readLine();

                        if (StringUtils.startsWith(line,"command:")){
                            command=StringUtils.removeStart(line, "command:");

                            if (brNet.ready()) {
                                line = brNet.readLine();
                                if (StringUtils.startsWith(line, "user:")) {
                                    user = StringUtils.removeStart(line, "user:");

                                    if (brNet.ready()) {
                                        line = brNet.readLine();
                                        if (StringUtils.startsWith(line, "message:")) {
                                            message = StringUtils.removeStart(line, "message:");
                                        }
                                    }
                                }
                            }
                        }else continue;

                        /**Выделяет получателя при передаче сообщения
                         *
                         */
                        if (StringUtils.startsWith(command,"chattingTo:")){
                            receiver=StringUtils.removeStart(command, "chattingTo:");
                            command="chattingTo:";
                        }
                        send_response_for_request ( command,  user,  message, receiver);
                    }

                }
            }
            finally {
                incoming.close();
                log.info("{} - is closed " , incoming);

            }
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }

    }



    void send_response_for_request (String command, String user, String message, String receiver) {
        if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(message)) {
            Db db = new Db();
            switch (command){
                case "in account"->{
                    if ( echoServer.getUserListRegistration().containsKey(user)) {

                        if(echoServer.getUserListRegistration().get(user).equals(message)) {
                            if (echoServer.getUserListOnline().containsKey(user)) {
                                outNet.println("BUSY");
                                done=false;
                            } else {
                                echoServer.getUserListOnline().put(user, incoming);
                                outNet.println("OK");
                                log.info("{} - now online " , echoServer.getUserListOnline());
                                echoServer.getReferenceBook().put(user, true);

                                //true to db_users.is_onliner
                                db.updateOnline(user,true);

                                //разослать новый справочник всем онлайн
                                send_referenceBook ();
                            }
                        }else {
                            outNet.println("NoPassword");
                            done=false;
                        }
                    }else {
                        outNet.println("NoUser");
                        done=false;
                    }
                }
                case "registration"->{
                    if ( echoServer.getUserListRegistration().containsKey(user)){
                        outNet.println("BUSY");
                    }else{
                        echoServer.getUserListRegistration().put(user,message);
                        echoServer.getReferenceBook().put(user, false);
                        outNet.println("OK");

                        //add to db_users.users
                        db.addUser(user,message);

                        //разослать новый справочник всем онлайн
                        send_referenceBook ();

                    }
                    done=false;
                }
                case "chattingTo"->{

                }
                case "exit"->{
                    done=false;
                    echoServer.getUserListOnline().remove(user);
                    echoServer.getReferenceBook().put(user, false);
                    //false to db_users.is_onliner
                    db.updateOnline(user,false);
                }

            }
        }
    }

    String referenceBook_to_JSON (){
         HashMap<String, Boolean> map= echoServer.getReferenceBook();
        String referenceBookJson=null;
        ObjectMapper mapper = new ObjectMapper();

        // Converting map to a JSON payload as string
        try {
            referenceBookJson = mapper.writeValueAsString(map);
//                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            log.info("{} - reference Book " ,referenceBookJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }




        return referenceBookJson;
    }



    @SneakyThrows(InterruptedException.class)
    void  send_referenceBook ()  {
        Thread.sleep(100);
        String txt= "message:"+referenceBook_to_JSON ();
        System.out.println("__________________________________");
        echoServer.getUserListOnline().forEach((user,socket) -> {
           try{
               System.out.println(user);
               System.out.println(socket);
           PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
               System.out.println(out);
            out.println("command:referenceBook");
            out.println("user:server");
               System.out.println(txt);
            out.println(txt);

        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
        });
    }


    void send_message(Socket s,  String command,String from_user,String message,String to_use) throws InterruptedException {
        BufferedReader brNet;
        PrintWriter outNet;
        try{
            brNet = new BufferedReader(new InputStreamReader(s.getInputStream()));
            outNet = new PrintWriter(s.getOutputStream(), true);

            outNet.println("command:"+command);
            outNet.println("user:"+from_user);
            outNet.println("message"+message);
            outNet.flush();

            s.setSoTimeout(1000000000);
            Thread.sleep(100);
            if (brNet.ready()) {
                String response_command=brNet.readLine().trim();
                String response_user=brNet.readLine().trim();
                String response_message=brNet.readLine().trim();

//                if ()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
