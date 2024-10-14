package org.chatServ;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
class RequestCorrespondence {
    private String type;
    private String period;
    private String collocutor;

}
