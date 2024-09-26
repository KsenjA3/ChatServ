package org.chatServ;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name= "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="message",nullable = false)
    private String message;

    @Column(name="time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;

//    @Column(name="from_user")
//    private String from_user;
//    @Column(name="to_user")
//    private String to_user;

    @Column(name="is_got")
    private boolean is_got;

    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn (name="from_user", nullable = false)
    private Users from_user;

    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn (name="to_user", nullable = false)
    private Users to_user;


    public Messages(String message, LocalDateTime time, String from_user, String to_user, boolean is_got) {
        this.message = message;
        this.time = time;
        this.is_got = is_got;

//        this.from_user = from_user;
//        this.to_user = to_user;

    }

    public Messages() {
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }

//    public String getFrom_user() {
//        return from_user;
//    }
//
//    public String getTo_user() {
//        return to_user;
//    }

    public boolean isIs_got() {
        return is_got;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

//    public void setFrom_user(String from_user) {
//        this.from_user = from_user;
//    }
//
//    public void setTo_user(String to_user) {
//        this.to_user = to_user;
//    }

    public void setIs_got(boolean is_got) {
        this.is_got = is_got;
    }
}
