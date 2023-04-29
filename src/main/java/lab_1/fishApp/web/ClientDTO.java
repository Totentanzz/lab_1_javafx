package lab_1.fishApp.web;

import com.typesafe.config.Config;
import lab_1.fishApp.model.Fish;
import lombok.*;

import java.io.Serializable;
import java.util.LinkedList;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ClientDTO implements Serializable {

    private dtoType dtoType;
    private dtoObject dtoObject;
    private dtoReply serverMessage;
    private String clientName;
    private LinkedList<Fish> clientFishList;
    private LinkedList<String> clientList;
    private Config clientConfig;

    public enum dtoObject {
        CLIENT_LIST,
        CONFIG,
        OBJECTS;
    }

    public enum dtoType {
        CLIENT_REQUEST,
        CLIENT_REPLY,
        SERVER_REQUEST,
        SERVER_REPLY;
    }

    public enum dtoReply {
        SUCCESS,
        ERROR;
    }

}
