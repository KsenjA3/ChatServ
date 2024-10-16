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
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    @ManyToOne ( cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH })
    @JoinColumn (name="from_user", nullable = false)
    private Users fromUser;

    @OneToMany (cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH },
            fetch = FetchType.LAZY, mappedBy = "message")
    private Set<MessagesUsers> messagesUsers;

    public Messages( String mess) {
        this.mess = mess;
        this.timeSend = LocalDateTime.now();
        this.messagesUsers = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", mess='" + mess + '\'' +
                ", timeSend=" + timeSend +
                ", fromUser=" + fromUser +
                '}';
    }
}
