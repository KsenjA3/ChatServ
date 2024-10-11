package org.chatServ;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Setter;
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

import static java.lang.Thread.*;

@Log4j2

class ThreadedEchoHandler implements Runnable{
    private Socket incoming;

    @Setter
    private ThreadedEchoServer echoServer;

    protected PrintWriter outNet;
    protected BufferedReader brNet;
    private boolean done;

    @Setter
    private Db db;

    ThreadedEchoHandler(Socket s, ThreadedEchoServer echoServ){
        incoming=s;
        echoServer=echoServ;
        try { InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();
            brNet = new BufferedReader(new InputStreamReader(inStream));
            outNet = new PrintWriter(outStream, true);
        }
        catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public void run() {
        String line, command, user="", message="", receiver="";
        try {
            try {
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
System.out.println("-----------------------------------------------");
                        log.info("command = {}" , command);
                            if (brNet.ready()) {
                                line = brNet.readLine();
                                if (StringUtils.startsWith(line, "user:")) {
                                    user = StringUtils.removeStart(line, "user:");
                            log.info("sender = {}" , user);

                                    if (brNet.ready()) {
                                        line = brNet.readLine();
                                        if (StringUtils.startsWith(line, "message:")) {
                                            message = StringUtils.removeStart(line, "message:");
                                        log.info("message = {}" , message);
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
                        log.info("receiver = {}" , receiver);
                        send_response_for_request ( command,  user,  message, receiver);
                    }

                }
            }
            finally {
                outNet.close();
                brNet.close();
                incoming.close();
                log.info("{} - is closed " , incoming);

            }
        } catch (IOException e) {
            log.error(e);
            e.printStackTrace();
        }

    }

    void send_response_for_request (String command, String sender, String message, String receiver) {

        if (!StringUtils.isEmpty(sender) && !StringUtils.isEmpty(message)) {
            db = new Db("hibernate.cfg.xml");
            switch (command){
                case "in account"->{
                    inAccount (sender, message);
                }
                case "registration"->{
                    registrationAccount(sender, message);
                }
                case "chattingTo:"->{
//                    db.updateOnline(sender,true);

                    // fill table messages
                    List<String> receiversListNewMess=fillTableMessages(message,sender,receiver);
                    receiversListNewMess.forEach(receiverString->{
                        //посылаем инфо о непрочитанных сообщениях
                        if(echoServer.getUserListOnline().containsKey(receiverString)){
                            Socket s = echoServer.getUserListOnline().get(receiverString);
                            send_signal_newMessages( s,  receiverString);
                        }
                    });
                }
                case "exit"->{
                    exitAccount(sender);
                }

            }
        }
    }

    protected void inAccount (String sender,String  message) {
        if ( echoServer.getUserListRegistration().containsKey(sender)) {

            if(echoServer.getUserListRegistration().get(sender).equals(message)) {
                if (echoServer.getUserListOnline().containsKey(sender)) {
                    outNet.println("BUSY");
                    done=false;
                } else {
                    echoServer.getUserListOnline().put(sender, incoming);
                    outNet.println("OK");
                    log.info("{} - now online " , echoServer.getUserListOnline());
                    echoServer.getReferenceBook().put(sender, true);

                    //true to db_users.is_onliner
                    db.updateOnline(sender,true);

                    //разослать новый справочник всем онлайн
                    send_referenceBook ();
                    //инфо о наличии новых сообщениях
                    boolean isNewMessagesExist=db.isNewMessagesExist(sender);
                    if (isNewMessagesExist)
                        send_signal_newMessages( incoming,  sender);
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

    protected void registrationAccount(String sender,String  message){
//        System.out.println(echoServer.getUserListRegistration());
//        System.out.println(sender);
//        System.out.println(echoServer.getUserListRegistration().containsKey(sender));
//        System.out.println(echoServer.getUserListRegistration().get(sender).equals(message));


        if ( echoServer.getUserListRegistration().containsKey(sender)){
            outNet.println("BUSY");
        }else{
            echoServer.getUserListRegistration().put(sender,message);
            echoServer.getReferenceBook().put(sender, false);
            outNet.println("OK");

            //add to db_users.users
            db.addUser(sender,message);

            //разослать новый справочник всем онлайн
            send_referenceBook ();
        }
        done=false;
    }

    protected  void exitAccount(String sender){
        done=false;
        echoServer.getUserListOnline().remove(sender);
        echoServer.getReferenceBook().put(sender, false);
        //false to db_users.is_onliner
        db.updateOnline(sender,false);
    }

    protected  List<String>  fillTableMessages(String message,String sender,String stringReceiversList){
        List<String> receiversListNewMess;
        if (StringUtils.startsWith(stringReceiversList,"<html>") &&
            StringUtils.endsWith(stringReceiversList,"</html>") ){
            stringReceiversList=StringUtils.removeEnd(stringReceiversList,"</html>");
            stringReceiversList= StringUtils.removeStart(stringReceiversList,"<html>");

            if (stringReceiversList.equals(("to all"))){
                List<String> receiversList= new ArrayList<>();
                echoServer.getUserListRegistration().forEach((user_receiver,pass)->{
                    if (!user_receiver.equals(sender))
                        receiversList.add(user_receiver);
                });
                receiversListNewMess=  new ArrayList<>(receiversList);
                db.addMessage(message,sender,receiversList);
            }
            else {
                String[] massReceiversList =stringReceiversList.split("<br>");
                List<String> receiversList= Arrays.stream(massReceiversList).toList();
                db.addMessage(message,sender,receiversList);
                receiversListNewMess=  new ArrayList<>(receiversList);
            }
            return receiversListNewMess;
        }
        else return null;

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
    private void  send_referenceBook ()  {
        Thread.sleep(1000);
        String txt= ""+referenceBook_to_JSON ();
        echoServer.getUserListOnline().forEach((user,socket) -> {
            send_message(socket, "referenceBook","server",txt, user);
        });
    }

    private void send_signal_newMessages(Socket s, String receiver){
        String cmd="newMessages";
        String mess = "newMessages";
        send_message(s,cmd, "server", mess,receiver);
    }

    @SneakyThrows
    protected void send_message(Socket s, String command,String from_user,String message,String to_user) {
        try {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("command:" + command);
            out.println("user:" + from_user);
            out.println("message:" + message);
            out.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            s.setSoTimeout(1000000000);
            sleep(100);
            if (br.ready()) {
                String response_command = br.readLine().trim();
                String response_user = br.readLine().trim();
                String response_message = br.readLine().trim();

                if (response_command.equals("command:" + command) &&
                        response_user.equals("user:" + to_user) &&
                        response_message.equals("message:" + message)) {
                    System.out.println("+++++++++++++++++++++++++++++++++");
                    return;
                } else {
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println(response_command+"="+"command:" + command);
                    System.out.println(response_user+"="+"user:" + from_user);
                    System.out.println(response_message+"="+"message:" + message);

                    send_message(s, command, from_user, message,to_user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
