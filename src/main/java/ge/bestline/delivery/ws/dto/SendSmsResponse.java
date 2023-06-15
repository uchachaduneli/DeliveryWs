package ge.bestline.delivery.ws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

import static java.util.Map.entry;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSmsResponse {
    @JsonProperty("Success")
    private Boolean success;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Output")
    private String output;
    @JsonProperty("ErrorCode")
    private Integer errorCode;
    private Map<Integer, String> errorCodeDescs = Map.ofEntries(
            entry(0, "მესიჯი მიღებულია smsoffice -ს მიერ"),
            entry(10, "destination შეიცავს არაქართულ ნომრებს"),
            entry(20, "ბალანსი არასაკმარისია"),
            entry(40, "გასაგზავნი ტექსტი 160 სიმბოლოზე მეტია"),
            entry(60, "ბრძანებას აკლია content პარამეტრის მნიშვნელობა, გასაგზავნი ტექსტი"),
            entry(70, "ბრძანებას აკლია ნომრები"),
            entry(75, "ყველა ნომერი სტოპ სიაშია"),
            entry(76, "ყველა ნომერი არასწორი ფორმატითაა მოწოდებული"),
            entry(77, "ყველა ნომერი სტოპ სიაშია ან არასწორი ფორმატითაა მოწოდებული"),
            entry(80, "key -ს შესაბამისი მომხმარებელი ვერ მოიძებნა"),
            entry(110, "sender პარამეტრის მნიშვნელობა გაუგებარია"),
            entry(120, "გააქტიურეთ api -ის გამოყენების უფლება პროფილის გვერდზე"),
            entry(150, "sender არ იძებნება სისტემაში. შეამოწმეთ მართლწერა"),
            entry(500, "ბრძანებას აკლია key პარამეტრი"),
            entry(600, "ბრძანებას აკლია destination პარამეტრი"),
            entry(700, "ბრძანებას აკლია sender პარამეტრი"),
            entry(800, "ბრძანებას აკლია content პარამეტრი"),
            entry(-100, "დროებითი შეფერხება"));

    @Override
    public String toString() {
        return "SendSmsResponse { " +
                " Success=" + success +
                ", Message='" + message + '\'' +
                ", Output='" + output + '\'' +
                ", ErrorCode=" + errorCode +
                ", ErrorCodeDefinition=" + errorCodeDescs.get(errorCode) +
                '}';
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
