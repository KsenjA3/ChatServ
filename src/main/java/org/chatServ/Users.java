package org.chatServ;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name= "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="user_name",nullable = false)
    private String userName;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="is_online",nullable = false)
    private boolean isOnline;

    @OneToMany (mappedBy = "fromUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Messages> messagesFrom;

    @OneToMany (mappedBy = "toUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Messages> messagesTo;


    public Users() {   }

    public Users(String userName, String password, boolean isOnline) {
        this.userName = userName;
        this.password = password;
        this.isOnline = isOnline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<Messages> getMessagesFrom() {
        return messagesFrom;
    }

    public void setMessagesFrom(List<Messages> messagesFrom) {
        this.messagesFrom = messagesFrom;
    }

    public List<Messages> getMessagesTo() {
        return messagesTo;
    }

    public void setMessagesTo(List<Messages> messagesTo) {
        this.messagesTo = messagesTo;
    }

    public void addMessageFromUser(Messages mess) {
        if (messagesFrom==null)
            messagesFrom=new ArrayList<>();
        messagesFrom.add(mess);
        mess.setFromUser(this);
    }

    public void addMessageToUser(Messages mess) {
        if (messagesTo==null)
            messagesTo=new ArrayList<>();
        messagesTo.add(mess);
        mess.setToUser(this);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}
