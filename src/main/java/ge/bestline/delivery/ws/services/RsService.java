package ge.bestline.delivery.ws.services;

import ge.bestline.delivery.soapclient.*;
import ge.bestline.delivery.ws.Exception.ResourceNotFoundException;
import ge.bestline.delivery.ws.Exception.WaybillException;
import ge.bestline.delivery.ws.dto.*;
import ge.bestline.delivery.ws.entities.*;
import ge.bestline.delivery.ws.repositories.*;
import ge.bestline.delivery.ws.util.SOAPConnector;
import lombok.extern.log4j.Log4j2;
import org.apache.xerces.dom.ElementNSImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RsService {
    @Value("${data.rs.user}")
    private String rsUser;
    @Value("${data.rs.pass}")
    private String rsPass;
    private final SOAPConnector soapConnector;
    private final TransporterWaybillRepository transporterWaybillRepository;
    private final ParcelRepository parcelRepo;
    private final ParcelStatusHistoryRepo parcelStatusHistoryRepo;
    private final ChekInOutRepository chekInOutRepo;
    private final DeliveryDetailRepository delivDetRepo;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public RsService(SOAPConnector soapConnector,
                     TransporterWaybillRepository transporterWaybillRepository,
                     ParcelRepository parcelRepo, ParcelStatusHistoryRepo parcelStatusHistoryRepo,
                     ChekInOutRepository chekInOutRepo, DeliveryDetailRepository delivDetRepo) {
        this.soapConnector = soapConnector;
        this.transporterWaybillRepository = transporterWaybillRepository;
        this.parcelRepo = parcelRepo;
        this.parcelStatusHistoryRepo = parcelStatusHistoryRepo;
        this.chekInOutRepo = chekInOutRepo;
        this.delivDetRepo = delivDetRepo;
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
                saved.add(transporterWaybillRepository.save(w));
            }
        } catch (Exception e) {
            log.error("stopped on " + w1.toString());
            e.printStackTrace();
        }
    }

    private RsErrorCode wrapRsErrorCodeElementToObject(Element e) throws Exception {
        RsErrorCode errorCode = new RsErrorCode();
        Node mayBeNullNode = e.getElementsByTagName("ID").item(0);
        errorCode.setId(mayBeNullNode != null ? Integer.valueOf(mayBeNullNode.getTextContent()) : null);
        mayBeNullNode = e.getElementsByTagName("TEXT").item(0);
        errorCode.setText(mayBeNullNode != null ? mayBeNullNode.getTextContent() : null);
        return errorCode;
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
                Optional<Parcel> p = parcelRepo.findByBarCodeAndDeleted(wayBill.getWaybillComment(), 2);
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

    public RsErrorCode getErrorCodeReason(Integer errorId) {
        GetErrorCodes req = new GetErrorCodes();
        req.setSp(rsPass);
        req.setSu(rsUser);
        GetErrorCodesResponse response = (GetErrorCodesResponse) soapConnector.callWebService(req, "http://tempuri.org/get_error_codes");
        Document document = (Document) ((ElementNSImpl) response.getGetErrorCodesResult().getContent().get(0)).getOwnerDocument();
        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();
        //Here comes the root node
        Element root = document.getDocumentElement();
        //Get all ERROR_CODE ELEMENTS
        NodeList nList = document.getElementsByTagName("ERROR_CODE");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //read each WAYBILL's detail
                Element eElement = (Element) node;
                try {
                    if (wrapRsErrorCodeElementToObject(eElement).getId().equals(errorId)) {
                        return wrapRsErrorCodeElementToObject(eElement);
                    }
                } catch (Exception e) {
                    log.error("Waybill ErrorCodes Parsing Failed, dom element: " + eElement.getTextContent(), e);
                }
            }
        }
        return null;
    }

    //OK statusianebistvis zednadebis daxurva weibilis aidit an barkodit
    public void closeRsWaybill(Integer waybillId, String parcelBarCode) throws WaybillException, DatatypeConfigurationException, NumberFormatException {
        log.info("Started Closing Waybill(#" + parcelBarCode + ") with OK status");
        WayBill wayBill = null;
        if (waybillId != null) {
            wayBill = transporterWaybillRepository.findByIdAndStatusIdNot(waybillId.toString(), RsWaybillStatuses.CLOSED.getValue()).orElseThrow(() ->
                    new WaybillException("Can't find Waybill With This Barcode In Comment " + parcelBarCode));
        } else {
            wayBill = transporterWaybillRepository.findUnClosedByBarCodeInComment(parcelBarCode).orElseThrow(() ->
                    new WaybillException("Can't find Waybill With This Barcode In Comment " + parcelBarCode));
        }
        log.info("Waybill For Close is found with this barCode:" + parcelBarCode + " waybillId:" + wayBill.getId() + " Calling RS Service To Close Waybill");
        CloseWaybillTransporter closeWaybillTransporterReq = new CloseWaybillTransporter();
        closeWaybillTransporterReq.setSp(rsPass);
        closeWaybillTransporterReq.setSu(rsUser);
        closeWaybillTransporterReq.setWaybillId(Integer.valueOf(wayBill.getId()));
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        closeWaybillTransporterReq.setDeliveryDate(date2);
        CloseWaybillTransporterResponse closeWaybillTransporterResponse =
                (CloseWaybillTransporterResponse) soapConnector.callWebService(closeWaybillTransporterReq, "http://tempuri.org/close_waybill_transporter");
        if (closeWaybillTransporterResponse.getCloseWaybillTransporterResult() != 1) {
            RsErrorCode rsErrorCode = getErrorCodeReason(closeWaybillTransporterResponse.getCloseWaybillTransporterResult());
            log.error("Can't Close Waybill Rs Service Returned With Error " + rsErrorCode.toString());
            throw new WaybillException(rsErrorCode.getText());
        } else {
            log.info("Waybill With ID: " + wayBill.getId() + " Closed Successfully");
        }
        log.info("Waybill(#" + parcelBarCode + ") with OK status Closed On RS");
    }

    // sync waybill transporter details - driver, carnumber, date to rs
    public void wayBillTransporterSyncToRs() {
        // map of current days barCode-wayBillId pairs from waybills parsed comment
        Map<String, Integer> barcodesFromWaybillComments = new HashMap<>();
        List<Object[]> tmp = transporterWaybillRepository.getBarCodesFromCurrentDayWaybillsComment();
        for (Object[] ob : tmp) {
            barcodesFromWaybillComments.put((String) ob[0], Integer.valueOf(ob[1].toString()));
        }
        // sync PU
        syncParcelsWithPUStatusesToRs(barcodesFromWaybillComments);
        //  sync WC
        syncParcelsWithWCStatusesToRs(barcodesFromWaybillComments);

        // OK statusianebis listi, mimdinare dgis
        log.info("Starting Rs Sync For OK status");
        List<Parcel> parcelsBarCodesWithOkStatus = parcelRepo.findByBarCodeInAndDeletedAndStatusIdIn(barcodesFromWaybillComments.keySet(), 2, new HashSet<Integer>(StatusReasons.OK1.getOkStatusIdes()));
        for (Parcel p : parcelsBarCodesWithOkStatus) {
            try {
                closeRsWaybill(barcodesFromWaybillComments.get(p.getBarCode()), null);
            } catch (Exception e) {
                log.error(e);
            }
        }
        log.info("Finished Rs Sync For OK status");

    }

    public void syncParcelsWithWCStatusesToRs(Map<String, Integer> barcodesFromWaybillComments) {
        log.info("Starting Rs Sync For WC status");
        // WC statusianebis listi komentaridan amoparsuli weibilebidan
        List<Parcel> parcelsBarCodesWithWCStatus = parcelRepo.findByBarCodeInAndDeletedAndStatusIdIn(barcodesFromWaybillComments.keySet(), 2, new HashSet<Integer>(Arrays.asList(StatusReasons.WC.getStatus().getId())));
        // for per parcel barcode gets car number from latest delivery detail record of this parcel
        //barcode - car_number pairs
        List<DeliveryDetailDTO> parcelsWCToSync =
                delivDetRepo.findParcelsLatestDetailsPairsForCarNumbers(parcelsBarCodesWithWCStatus.stream().map(Parcel::getBarCode).collect(Collectors.toList()));
        for (DeliveryDetailDTO p : parcelsWCToSync) {
            try {
                SaveWaybillTransporter saveWaybillTranspRequest = new SaveWaybillTransporter();
                saveWaybillTranspRequest.setSp(rsPass);
                saveWaybillTranspRequest.setSu(rsUser);
                saveWaybillTranspRequest.setWaybillId(barcodesFromWaybillComments.get(p.getParcelBarCode()));
                saveWaybillTranspRequest.setCarNumber(p.getCarNumber());
                saveWaybillTranspRequest.setDriverTin(p.getCourierIdentNum());
                saveWaybillTranspRequest.setDriverName(p.getCourierDesc());
                saveWaybillTranspRequest.setTransId(1); // es 1-ani saavtomobilo gadazidvis kodia
                saveWaybillTranspRequest.setChekDriverTin(1); // 0 ucxoetis moqalaqe 1 saqartvelos moqalaqe
                log.info("Calling WC Rs Sync With Params: " + saveWaybillsReqToString(saveWaybillTranspRequest));
                SaveWaybillTransporterResponse response = (SaveWaybillTransporterResponse) soapConnector.callWebService(saveWaybillTranspRequest,
                        "http://tempuri.org/save_waybill_transporter");
                if (response.getSaveWaybillTransporterResult() != 1) {
                    RsErrorCode rsErrorCode = getErrorCodeReason(response.getSaveWaybillTransporterResult());
                    throw new WaybillException("Can't Save Waybill for WC status of parcel " + p.getParcelBarCode() + " Rs Returns error: " + rsErrorCode.toString());
                } else {
                    log.info("Rs Waybill Transporter Save(rs service) for Parcel " + p.getParcelBarCode() + " with WC status finished successfully, Now Calling 'send_waybill_transporter'");
                    SendWaybillTransporter sendWaybillTransReq = new SendWaybillTransporter();
                    sendWaybillTransReq.setSp(rsPass);
                    sendWaybillTransReq.setSu(rsUser);
                    sendWaybillTransReq.setWaybillId(barcodesFromWaybillComments.get(p.getParcelBarCode()));
                    XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
                    sendWaybillTransReq.setBeginDate(date);
                    SendWaybillTransporterResponse resp = (SendWaybillTransporterResponse) soapConnector.callWebService(sendWaybillTransReq
                            , "http://tempuri.org/send_waybill_transporter");
                    if (resp.getSendWaybillTransporterResult() != 1) {
                        RsErrorCode rsErrorCode = getErrorCodeReason(resp.getSendWaybillTransporterResult());
                        throw new WaybillException("Can't Sync Rs Send Waybill Transporter for WC status of parcel " + p.getParcelBarCode() + " Rs Returns error: " + rsErrorCode.toString());
                    } else {
                        log.info("Rs Sync for Parcel: " + p.getParcelBarCode() + " with status WC completed successfully");
                    }
                }
            } catch (ResourceNotFoundException e) {
                log.error("Syncing WC status to RS -" + p.getParcelBarCode() + " " + e.getMessage(), e);
            } catch (WaybillException e) {
                log.error("Syncing WC status to RS -" + p.getParcelBarCode(), e);
            } catch (DatatypeConfigurationException e) {
                log.error("Calling send_waybill_transporter failed ", e);
            }
        }
        log.info("Finished Rs Sync For WC status");
    }

    public void syncParcelsWithPUStatusesToRs(Map<String, Integer> barcodesFromWaybillComments) {
        log.info("Starting Rs Sync For PU status");
        // PU statusianebis listi, mimdinare dgis
        List<Parcel> parcelsWithPUStatus = parcelRepo.findByBarCodeInAndDeletedAndStatusIdIn(barcodesFromWaybillComments.keySet(), 2, new HashSet<Integer>(Arrays.asList(StatusReasons.PU.getStatus().getId())));
        for (Parcel p : parcelsWithPUStatus) {
            try {
                //getting courier who set status PU
                ParcelStatusHistory psh = parcelStatusHistoryRepo.findTheLastWithParcelIdAndStatusCode(p.getId(), "PU").orElseThrow(() ->
                        new ResourceNotFoundException("Can't find Courier Who Set PU On parcel " + p.getBarCode() + " to sync his car number with RS"));
                //getting couriers last car's number when going out
                CourierCheckInOut courierCheckInOut = chekInOutRepo.findCouriersLastCheckoutRecord(psh.getOperUSer().getId()).orElseThrow(() ->
                        new ResourceNotFoundException("Can't find Couriers checkout operation to get car number for Rs sync, courier:  "
                                + psh.getOperUSer().getName() + " " + psh.getOperUSer().getLastName() + " " + psh.getOperUSer().getPersonalNumber()));
                SaveWaybillTransporter saveWaybillTranspRequest = new SaveWaybillTransporter();
                saveWaybillTranspRequest.setSp(rsPass);
                saveWaybillTranspRequest.setSu(rsUser);
                saveWaybillTranspRequest.setWaybillId(barcodesFromWaybillComments.get(p.getBarCode()));
                saveWaybillTranspRequest.setCarNumber(courierCheckInOut.getCarNumber());
                saveWaybillTranspRequest.setDriverTin(courierCheckInOut.getCourier().getPersonalNumber());
                saveWaybillTranspRequest.setDriverName(courierCheckInOut.getCourier().getName() + " " + courierCheckInOut.getCourier().getLastName());
                saveWaybillTranspRequest.setTransId(1); // es 1-ani saavtomobilo gadazidvis kodia
                saveWaybillTranspRequest.setChekDriverTin(1); // 0 ucxoetis moqalaqe 1 saqartvelos moqalaqe
                log.info("Calling PU Rs Sync With Params: " + saveWaybillsReqToString(saveWaybillTranspRequest));
                SaveWaybillTransporterResponse response = (SaveWaybillTransporterResponse) soapConnector.callWebService(saveWaybillTranspRequest,
                        "http://tempuri.org/save_waybill_transporter");
                if (response.getSaveWaybillTransporterResult() != 1) {
                    RsErrorCode rsErrorCode = getErrorCodeReason(response.getSaveWaybillTransporterResult());
                    throw new WaybillException("Can't Save Waybill for PU status of parcel " + p.getBarCode() + " Rs Returns error: " + rsErrorCode.toString());
                } else {
                    log.info("Rs Waybill Transporter Save(rs service) for Parcel " + p.getBarCode() + " with PU status finished successfully, Now Calling 'send_waybill_transporter'");
                    SendWaybillTransporter sendWaybillTransReq = new SendWaybillTransporter();
                    sendWaybillTransReq.setSp(rsPass);
                    sendWaybillTransReq.setSu(rsUser);
                    sendWaybillTransReq.setWaybillId(barcodesFromWaybillComments.get(p.getBarCode()));
                    XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
                    sendWaybillTransReq.setBeginDate(date2);
                    SendWaybillTransporterResponse resp = (SendWaybillTransporterResponse) soapConnector.callWebService(sendWaybillTransReq
                            , "http://tempuri.org/send_waybill_transporter");
                    if (resp.getSendWaybillTransporterResult() != 1) {
                        RsErrorCode rsErrorCode = getErrorCodeReason(resp.getSendWaybillTransporterResult());
                        throw new WaybillException("Can't Sync Rs Send Waybill Transporter for PU status of parcel " + p.getBarCode() + " Rs Returns error: " + rsErrorCode.toString());
                    } else {
                        log.info("Rs Sync for Parcel: " + p.getBarCode() + " with status PU completed successfully");
                    }
                }
            } catch (ResourceNotFoundException e) {
                log.error("Syncing PU status to RS - parcel:" + p.getBarCode() + e.getMessage(), e);
            } catch (WaybillException e) {
                log.error("Syncing PU status to RS - parcel:" + p.getBarCode(), e);
            } catch (DatatypeConfigurationException e) {
                log.error("Syncing PU status to RS - Calling send_waybill_transporter failed for parcel " + p.getBarCode(), e);
            }
        }
        log.info("Finished Rs Sync For PU status");
    }

    private String saveWaybillsReqToString(SaveWaybillTransporter req) {
        return "{ " +
                "wayBillId: " + req.getWaybillId() +
                " carNumber: " + req.getCarNumber() +
                " DriverPersNum: " + req.getDriverTin() +
                " Driver: " + req.getDriverName() +
                " }";
    }
}


