package ge.bestline.delivery.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsErrorCode {
    private Integer id;
    private String text;

    @Override
    public String toString() {
        return "RsErrorCode{" +
                "ErrorID=" + id +
                ", Error Message='" + text + '\'' +
                '}';
    }
}
