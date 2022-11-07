package ge.bestline.delivery.ws.services;

import ge.bestline.delivery.soapclient.GetTransporterWaybills;
import ge.bestline.delivery.soapclient.GetTransporterWaybillsResponse;
import ge.bestline.delivery.ws.dto.RsSyncStatus;
import ge.bestline.delivery.ws.entities.Parcel;
import ge.bestline.delivery.ws.entities.WayBill;
import ge.bestline.delivery.ws.entities.WayBillStatus;
import ge.bestline.delivery.ws.entities.WayBillType;
import ge.bestline.delivery.ws.repositories.ParcelRepository;
import ge.bestline.delivery.ws.repositories.TranporterWaybillRepository;
import ge.bestline.delivery.ws.util.SOAPConnector;
import lombok.extern.log4j.Log4j2;
import org.apache.xerces.dom.ElementNSImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Service
public class RsService {
    @Value("${data.rs.user}")
    private String rsUser;
    @Value("${data.rs.pass}")
    private String rsPass;
    private final SOAPConnector soapConnector;
    private final TranporterWaybillRepository tranporterWaybillRepository;
    private final ParcelRepository parcelRepo;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public RsService(SOAPConnector soapConnector,
                     TranporterWaybillRepository tranporterWaybillRepository,
                     ParcelRepository parcelRepo) {
        this.soapConnector = soapConnector;
        this.tranporterWaybillRepository = tranporterWaybillRepository;
        this.parcelRepo = parcelRepo;
    }

    public void syncWayBills() throws Exception {
        List<WayBill> wayBillList = new ArrayList<>();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -30);
        XMLGregorianCalendar xFromCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        cal.setTime(new Date());
        XMLGregorianCalendar xToCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        GetTransporterWaybills getWaybill = new GetTransporterWaybills();
        getWaybill.setSp(rsPass);
        getWaybill.setSu(rsUser);
        getWaybill.setCreateDateS(xFromCal);
        getWaybill.setCreateDateE(xToCal);
        GetTransporterWaybillsResponse response = (GetTransporterWaybillsResponse)
                soapConnector.callWebService(getWaybill, "http://tempuri.org/get_transporter_waybills");

        Document document = (Document) ((ElementNSImpl) response.getGetTransporterWaybillsResult().getContent().get(0)).getOwnerDocument();
        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();
        //Here comes the root node
        Element root = document.getDocumentElement();
        //Get all waybills
        NodeList nList = document.getElementsByTagName("WAYBILL");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //read each WAYBILL's detail
                Element eElement = (Element) node;
                try {
                    wayBillList.add(wrapWaybillElementToEntityObject(eElement));
                } catch (Exception e) {
                    log.error("Waybill Fields Parsing Failed, dom element: " + eElement.getTextContent(), e);
                }
            }
        }
        log.info("Waybill Data Parsing Finished, " + wayBillList.size() + " waybills data has got");
        List<WayBill> saved = new ArrayList<>();
        WayBill w1 = null;
        try {
            for (WayBill w : wayBillList) {
                w1 = w;
                saved.add(tranporterWaybillRepository.save(w));
            }
        } catch (Exception e) {
            log.error("stopped on " + w1.toString());
            e.printStackTrace();
        }
    }

    private WayBill wrapWaybillElementToEntityObject(Element e) throws Exception {
        WayBill wayBill = new WayBill();
        // to avoid nullpointer Exception - some of these fields value sometimes is null
        Node mayBeNullNode = e.getElementsByTagName("ID").item(0);
        wayBill.setId(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("TYPE").item(0);
        wayBill.setType(mayBeNullNode != null ? new WayBillType(Integer.valueOf(mayBeNullNode.getTextContent())) : null);
        mayBeNullNode = e.getElementsByTagName("CREATE_DATE").item(0);
        wayBill.setRsCreateDate(mayBeNullNode != null ? dateFormat.parse(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("BUYER_TIN").item(0);
        wayBill.setBuyerTin(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("BUYER_NAME").item(0);
        wayBill.setBuyerName(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("START_ADDRESS").item(0);
        wayBill.setStartAddress(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("END_ADDRESS").item(0);
        wayBill.setEndAddress(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("DRIVER_TIN").item(0);
        wayBill.setDriverTin(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("DRIVER_NAME").item(0);
        wayBill.setDriverName(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("CAR_NUMBER").item(0);
        wayBill.setCarNumber(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("TRANSPORT_COAST").item(0);
        wayBill.setTransportCoast(mayBeNullNode != null ? Double.valueOf(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("STATUS").item(0);
        wayBill.setStatus(mayBeNullNode != null ? new WayBillStatus(Integer.valueOf(mayBeNullNode.getTextContent())) : null);
        mayBeNullNode = e.getElementsByTagName("ACTIVATE_DATE").item(0);
        wayBill.setActivateDate(mayBeNullNode != null ? dateFormat.parse(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("FULL_AMOUNT").item(0);
        wayBill.setFullAmount(mayBeNullNode != null ? Double.valueOf(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("WAYBILL_NUMBER").item(0);
        wayBill.setWaybillNumber(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        mayBeNullNode = e.getElementsByTagName("S_USER_ID").item(0);
        wayBill.setSUserId(mayBeNullNode != null ? Integer.valueOf(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("BEGIN_DATE").item(0);
        wayBill.setBeginDate(mayBeNullNode != null ? dateFormat.parse(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("WAYBILL_COMMENT").item(0);
        if (mayBeNullNode != null) {
            wayBill.setWaybillComment(mayBeNullNode.getTextContent().trim());
            try {
                Optional<Parcel> p = parcelRepo.findByBarCode(wayBill.getWaybillComment());
                if (p.isPresent()) {
                    wayBill.setSyncStatus(RsSyncStatus.IsProcessing.getValue());
                } else {
                    wayBill.setSyncStatus(RsSyncStatus.NoParcesFound.getValue());
                }
            } catch (Exception ex) {
                log.warn("Can't Find Parcel With Waybill Comment's Content: " + wayBill.getWaybillComment(), ex);
            }
        } else {
            wayBill.setSyncStatus(RsSyncStatus.NoBarCodeIntoComment.getValue());
        }
        mayBeNullNode = e.getElementsByTagName("IS_CONFIRMED").item(0);
        wayBill.setIsConfirmed(mayBeNullNode != null ? Integer.valueOf(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("IS_CORRECTED").item(0);
        wayBill.setIsCorrected(mayBeNullNode != null ? Integer.valueOf(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("BUYER_ST").item(0);
        wayBill.setBuyerSt(mayBeNullNode != null ? Integer.valueOf(mayBeNullNode.getTextContent()) : null);
        return wayBill;
    }
}
