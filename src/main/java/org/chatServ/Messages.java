package org.chatServ;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.grammars.hql.HqlParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name= "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="message",nullable = false)
    private String mess;

    @CreationTimestamp
    @Basic
    @Column(name="time_send", nullable = false)
    private LocalDateTime timeSend;

    @Column(name="is_got")
    private boolean isGot;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="time_receive")
    private LocalDateTime timeReceive;

    @PreUpdate
    protected void onUpdate () {
        timeReceive = LocalDateTime.now();
    }

    @ManyToOne ( cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH })
    @JoinColumn (name="from_user", nullable = false)
    private Users fromUser;



    @ManyToMany (cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH })
    @JoinTable (name="mess_to_user",
            joinColumns = @JoinColumn (name="messages",referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn (name="to_users",referencedColumnName = "id", nullable = false) )
    private List<Users> toUsers;




    public Messages() {  }

    public Messages( String mess) {
        this.mess = mess;
        this.timeSend = LocalDateTime.now();
        this.isGot = false;
    }



    public void add_oneUser_to_Message(Users user) {
        if (toUsers==null)
            toUsers=new ArrayList<>();
        toUsers.add(user);
//        user.add_oneMessage_to_FromUser(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public LocalDateTime getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(LocalDateTime timeSend) {
        this.timeSend = timeSend;
    }

    public boolean isGot() {
        return isGot;
    }

    public void setGot(boolean got) {
        isGot = got;
    }

    public LocalDateTime getTimeReceive() {
        return timeReceive;
    }

    public void setTimeReceive(LocalDateTime timeReceive) {
        this.timeReceive = timeReceive;
    }

    public Users getFromUser() {
        return fromUser;
    }

    public void setFromUser(Users fromUser) {
        this.fromUser = fromUser;
    }

    public List<Users> getToUsers() { return toUsers;  }

    public void setToUsers(List<Users> toUsers) { this.toUsers = toUsers; }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", mess='" + mess + '\'' +
                ", timeSend=" + timeSend +
                ", isGot=" + isGot +
                ", timeReceive=" + timeReceive +
                ", fromUser=" + fromUser +
//                ", toUser=" + toUser +
                '}';
    }
}
