package org.chatServ;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessagesUsersID implements Serializable {


    @ManyToOne
    private Messages messageId;

    @ManyToOne
    private Users userId;

    @Override
    public String toString() {
        return "MessagesUsersID{" +
                "messageId=" + messageId +
                ", userId=" + userId +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagesUsersID that = (MessagesUsersID) o;
        if (messageId != null ? !messageId.equals(that.messageId) : that.messageId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = (messageId != null ? messageId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

}
