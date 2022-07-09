package ge.bestline.delivery.ws.controllers;

import ge.bestline.delivery.ws.util.SOAPConnector;
import ge.bestline.delivery.soapclient.GetWaybill;
import ge.bestline.delivery.soapclient.GetWaybillResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rs")
public class RsController {
    @Value("${data.rs.user}")
    private String rsUser;
    @Value("${data.rs.pass}")
    private String rsPass;
    private final SOAPConnector soapConnector;

    public RsController(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }

    @GetMapping
    public ResponseEntity<GetWaybillResponse> getWaybill() {
        String res = "";
        GetWaybill getWaybill = new GetWaybill();
        getWaybill.setWaybillId(711372259);
        getWaybill.setSp(rsPass);
        getWaybill.setSu(rsUser);
        GetWaybillResponse response = (GetWaybillResponse) soapConnector.callWebService(getWaybill, "http://tempuri.org/get_waybill");
        System.out.println(response.toString());
        return ResponseEntity.ok(response);
    }
}
