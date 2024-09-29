package org.chatServ;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name= "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="message",nullable = false)
    private String message;

    @Column(name="time_send", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time_send;

    @Column(name="is_got")
    private boolean is_got;

    @Column(name="time_receive")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time_receive;

    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn (name="from_user", nullable = false)
    private Users from_user;
//
//    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn (name="to_user", nullable = false)
//    private Users to_user;


    public Messages(String message, LocalDateTime time_send, boolean is_got, LocalDateTime time_receive) {
        this.message = message;
        this.time_send = time_send;
        this.time_receive = time_receive;
        this.is_got = is_got;
    }

    public Messages(String message, LocalDateTime time_send, boolean is_got) {
        this.message = message;
        this.time_send = time_send;
        this.is_got = is_got;
    }

    public Messages() {  }

    public Users getFrom_user() { return from_user; }

    public void setFrom_user(Users from_user) {  this.from_user = from_user;  }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isIs_got() {
        return is_got;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIs_got(boolean is_got) {
        this.is_got = is_got;
    }

    public LocalDateTime getTime_receive() { return time_receive;  }

    public LocalDateTime getTime_send() { return time_send; }

    public void setTime_receive(LocalDateTime time_receive) {this.time_receive = time_receive; }

    public void setTime_send(LocalDateTime time_send) { this.time_send = time_send; }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", time_send=" + time_send +
                ", is_got=" + is_got +
                ", time_receive=" + time_receive +
                ", from_user=" + from_user +
                '}';
    }
}
