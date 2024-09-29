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
    private String user_name;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="is_online",nullable = false)
    private boolean is_online;

    @OneToMany (mappedBy = "from_user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn (name = "message_id", unique = true)
    private List<Messages> messages;


    public Users() {   }

    public Users(String user_name, String password, boolean is_online) {
        this.user_name = user_name;
        this.password = password;
        this.is_online = is_online;
    }

    public int getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isIs_online() {
        return is_online;
    }

    public void setId(int id) { this.id = id;  }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIs_online(boolean is_online) {
        this.is_online = is_online;
    }

    public List<Messages> getMessages() { return messages;  }

    public void setMessages(List<Messages> messages) {   this.messages = messages;  }

    public void addMessageFromUser( Messages mess) {
        if (messages==null)
            messages=new ArrayList<>();
        messages.add(mess);
        mess.setFrom_user(this);
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", user_name='" + user_name + '\'' +
                ", password='" + password + '\'' +
                ", is_online=" + is_online +
//                ", messages=" + messages +
                '}';
    }
}
