package org.chatServ;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Cache;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name= "messages")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Messages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="message",nullable = false)
    private String mess;

    @CreationTimestamp
    @Basic
    @Column(name="time_send", nullable = false)
    private LocalDateTime timeSend;

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

    @OneToMany (cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH },
            fetch = FetchType.LAZY, mappedBy = "mu.messageId")
    private Set<MessagesUsers> messagesUsers = new HashSet<>();

    public Messages( String mess) {
        this.mess = mess;
        this.timeSend = LocalDateTime.now();
        this.messagesUsers = new HashSet<>();
    }

    public void add_oneUser_to_Message(Users user) {
        MessagesUsers messagesUser= new MessagesUsers(this,user);
        messagesUsers.add(messagesUser);
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", mess='" + mess + '\'' +
                ", timeSend=" + timeSend +
                ", timeReceive=" + timeReceive +
                ", fromUser=" + fromUser +
                '}';
    }
}
