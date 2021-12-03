package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.Message;
import ge.bestline.delivery.ws.entities.MessageCC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO extends Message {
    List<MessageCC> cc;
}
