package org.chatServ;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
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

    @Embedded
    @AssociationOverrides({
            @AssociationOverride(name = "mu.messageId", joinColumns = @JoinColumn(name = "message_id")),
            @AssociationOverride(name = "mu.userId", joinColumns = @JoinColumn(name = "user_id"))
    })
    private MessagesUsersID mu = new MessagesUsersID ();

    @Column(name="is_got")
    private boolean isGot;


    @Transient
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private  Users user;

    @Transient
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    private   Messages message;

    public MessagesUsers(Messages message, Users user){
        this.message=message;
        this.user=user;
    }

    @Override
    public String toString() {
        return "MessagesUsers{" +
                "id=" + id +
                ", mu=" + mu +
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


//    @Transient
//    public Messages getMessages () {
//        return getMu().getMessageId();
//    }
//    public void setMessages (Messages message) {
//        getMu().setMessageId(message);
//    }
//
//    @Transient
//    public Users getUsers () {
//        return getMu().getUserId();
//    }
//    public void setUsers (Users user) {
//        getMu().setUserId(user);
//    }
//
//    public boolean equals(Object o) {
//        if (this == o)
//            return true;
//        if (o == null || getClass() != o.getClass())
//            return false;
//
//        MessagesUsers that = (MessagesUsers) o;
//
//        if (getMu() != null ? !getMu().equals(that.getMu())
//                : that.getMu() != null)
//            return false;
//
//        return true;
//    }
//
//    public int hashCode() {
//        return (getMu() != null ? getMu().hashCode() : 0);
//    }
}
