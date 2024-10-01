package org.chatServ;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name= "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="message",nullable = false)
    private String mess;

    @Column(name="time_send", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeSend;

    @Column(name="is_got")
    private boolean isGot;

    @Column(name="time_receive")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timeReceive;

    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn (name="from_user", nullable = false)
    private Users fromUser;

    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn (name="to_user", nullable = false)
    private Users toUser;




    public Messages() {  }

    public Messages(String mess, LocalDateTime timeSend, boolean isGot, LocalDateTime timeReceive) {
        this.mess = mess;
        this.timeSend = timeSend;
        this.isGot = isGot;
        this.timeReceive = timeReceive;
    }

    public Messages(String mess, LocalDateTime timeSend, boolean isGot) {
        this.mess = mess;
        this.timeSend = timeSend;
        this.isGot = isGot;
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

    public Users getToUser() {
        return toUser;
    }

    public void setToUser(Users toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", mess='" + mess + '\'' +
                ", timeSend=" + timeSend +
                ", isGot=" + isGot +
                ", timeReceive=" + timeReceive +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                '}';
    }
}
