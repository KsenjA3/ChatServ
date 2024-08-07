package org.chatServ;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

                    /**
                     * Формирует полный запрос от клиента:
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
                                        if (StringUtils.startsWith(line, "message")) {
                                            message = StringUtils.removeStart(line, "message");
                                        }
                                    }
                                }
                            }
                        }else continue;

                        /**Выделяет получателя при передаче сообщения
                         *
                         */
                        if (StringUtils.startsWith(command,"chattingTo")){
                            receiver=StringUtils.removeStart(command, "chattingTo");
                            command="chattingTo";
                        }

                        send_response_for_request ( command,  user,  message, receiver);



                    }


                }


            } finally {
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
                                System.out.println("now online- "+echoServer.getUserListOnline());
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
                        outNet.println("OK");
                    }
                    done=false;
                }
                case "chattingTo"->{

                }
            }
        }
    }


}
