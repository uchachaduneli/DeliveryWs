package ge.ufc.authorizations.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Log4j2
@Component
public class LoggerHelper {

  public double getExecutionTime(long startTime, long endTime) {
    long totalTime = endTime - startTime;
    double second = (double) totalTime / 1000000000.0;
    DecimalFormat df = new DecimalFormat("#.###");
    second = Double.valueOf(df.format(second));
    return second;
  }

  public String buildExecuteTime(long startTime) {
    return " executeTime=" + getExecutionTime(startTime, System.nanoTime());
  }

  public <T> String generateJson(T object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("Can't get Json value of Object To Log", e);
      return null;
    }
  }

  public String buildAlertMessage(String trapMessage) {
    return " [[[ " + trapMessage + " ]]]";
  }

  public String buildMessage(String message) {
    return " {{{ " + message + " }}}";
  }
}
