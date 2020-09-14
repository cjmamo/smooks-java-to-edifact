package org.smooks.examples.java2edifact;

import com.ibm.dfdl.edi.un.edifact.d03b.*;
import com.ibm.dfdl.edi.un.service._4.*;
import org.smooks.Smooks;
import org.smooks.payload.ByteSource;
import org.smooks.payload.StringResult;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.io.*;
import java.math.BigDecimal;

public class Main {
    
    public static void main(String[] args) throws IOException, SAXException, JAXBException {
        // Build Java model        
        Interchange interchange = new Interchange().
                withUNA(new UNA().
                        withCompositeSeparator(":").
                        withFieldSeparator("+").
                        withDecimalSeparator(".").
                        withEscapeCharacter("?").
                        withRepeatSeparator("*").
                        withSegmentTerminator("'")).
                withUNB(new UNBInterchangeHeader().
                        withS001(new S001SyntaxIdentifier().
                                withE0001(E0001SyntaxIdentifier.UNOC).withE0002("4")).
                        withS002(new S002InterchangeSender().
                                withE0004("5790000274017").
                                withE0007("14")).
                        withS003(new S003InterchangeRecipient().
                                withE0010("5708601000836").
                                withE0007("14")).
                        withS004(new S004DateAndTimeOfPreparation().
                                withE0017(new BigDecimal(990420)).
                                withE0019(new BigDecimal(1137))).
                        withE0020("17").
                        withS005(new S005RecipientReferencePasswordDetails().withE0022("")).
                        withE0026("INVOIC").
                        withE0035(new BigDecimal(1))).
                withMessage(new Message().
                        withContent(new JAXBElement<>(new QName("UNH"), UNHMessageHeader.class, new UNHMessageHeader().
                                withE0062("30").
                                withS009(new S009MessageIdentifier().
                                        withE0065(E0065MessageType.INVOIC).
                                        withE0052("D").
                                        withE0054("03B").
                                        withE0051(E0051ControllingAgencyCoded.UN)))).
                        withContent(new JAXBElement<>(new QName("http://www.ibm.com/dfdl/edi/un/edifact/D03B", "INVOIC", "D03B"), INVOIC.class, new INVOIC().
                                withBGM(new BGMBeginningOfMessage().
                                        withC002(new C002DocumentMessageName().withE1001("380")).
                                        withC106(new C106DocumentMessageIdentification().withE1004("539602"))).
                                withDTM(new DTMDateTimePeriod().
                                        withC507(new C507DateTimePeriod().
                                                withE2005("137").
                                                withE2380("19990420").
                                                withE2379("102"))).
                                withUNS(new UNSSectionControl().
                                        withE0081(E0081SectionIdentification.S)).
                                withSegGrp50(new INVOIC.SegGrp50().
                                        withMOA(new MOAMonetaryAmount().
                                                withC516(new C516MonetaryAmount().
                                                        withE5025("64").
                                                        withE5004(new BigDecimal("100.95")).
                                                        withE6345("GBP")))))).
                        withContent(new JAXBElement<>(new QName("UNT"), UNTMessageTrailer.class, new UNTMessageTrailer().
                                withE0074(new BigDecimal(36)).
                                withE0062("30")))).
                withUNZ(new UNZInterchangeTrailer().
                        withE0036(new BigDecimal(1)).
                        withE0020("17"));

        // Turn Java model into XML       
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance(Interchange.class, com.ibm.dfdl.edi.un.service._4.ObjectFactory.class);
        jaxbContext.createMarshaller().marshal(interchange, byteArrayOutputStream);

        // Turn XML into EDIFACT
        final Smooks smooks = new Smooks("smooks-config.xml");
        StringResult stringResult = new StringResult();
        smooks.filterSource(new ByteSource(byteArrayOutputStream.toByteArray()), stringResult);
        
        System.out.println("\n" + stringResult.getResult());
    }
}
