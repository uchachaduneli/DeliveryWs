package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.WayBill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaybillDTO extends WayBill {

    private Date rsCreateDateTo;
    private String strRsCreateDate;
    private String strRsCreateDateTo;

    public static Timestamp convertStrDateToDateObj(String strDate) throws ParseException {
        Date tmpDate = (Date) new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(strDate.replace("T", " "));
        return new Timestamp(tmpDate.getTime());
    }
}
