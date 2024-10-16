package org.chatServ;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@Table(name = "messages_users")
@Entity
public class MessagesUsers implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="is_got")
    private boolean isGot;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="time_receive")
    private LocalDateTime timeReceive;

    @PreUpdate
    protected void onUpdate () {
        timeReceive = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private  Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private   Messages message;

    public MessagesUsers(Messages message, Users user){
        this.message=message;
        this.user=user;
        this.isGot=false;
    }

    @Override
    public String toString() {
        return "MessagesUsers{" +
                "id=" + id +
                ", isGot=" + isGot +
                ", user=" + user +
                ", message=" + message +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessagesUsers other = (MessagesUsers) obj;
        return Objects.equals(message, other.message) && Objects.equals(user, other.user);
    }

}
