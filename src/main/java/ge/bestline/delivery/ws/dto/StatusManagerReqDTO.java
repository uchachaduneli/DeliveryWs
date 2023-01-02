package ge.bestline.delivery.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusManagerReqDTO {
    private Integer statusId;
    private String note;
    private String strStatusDateTime;
    private Timestamp statusDateTime;
    private List<String> barCodes;
}
