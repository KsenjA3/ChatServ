package org.chatServ;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name= "users", uniqueConstraints = @UniqueConstraint(columnNames = "user_name"))
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Users implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @Column(name="user_name",unique = true, nullable = false)
    private String userName;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="is_online",nullable = false)
    private boolean isOnline;

    @OneToMany (mappedBy = "fromUser", fetch = FetchType.LAZY, cascade = {
                    CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH })
    private List<Messages> messagesFrom;

    @OneToMany (cascade = {
            CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH },
                fetch = FetchType.LAZY, mappedBy = "mu.userId")
    private Set<MessagesUsers> messagesUsers = new HashSet<>();

    public Users(String userName, String password, boolean isOnline) {
        this.userName = userName;
        this.password = password;
        this.isOnline = isOnline;
    }

    public void add_oneMessage_to_ToUser(Messages mess) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++");
        System.out.println(mess);
        System.out.println(this);
        MessagesUsers messagesUser= new MessagesUsers(mess,this);
        System.out.println(messagesUser);
        messagesUsers.add(messagesUser);
    }
    public void add_oneMessage_to_FromUser(Messages mess) {
        if (messagesFrom==null)
            messagesFrom=new ArrayList<>();
        messagesFrom.add(mess);
        mess.setFromUser(this);
    }

    public void add_oneMessage(Messages mess) {
        if (messagesFrom==null)
            messagesFrom=new ArrayList<>();
        messagesFrom.add(mess);
        mess.setFromUser(this);
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
