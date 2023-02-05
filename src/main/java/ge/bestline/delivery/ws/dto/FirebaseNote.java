package ge.bestline.delivery.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseNote {
    private String subject;
    private String content;
    private Map<String, String> data;

    public FirebaseNote(String subject, String content) {
        this.subject = subject;
        this.content = content;
        this.data = new HashMap<>();
    }
}
