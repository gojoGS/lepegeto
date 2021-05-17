package util.jaxb;

import java.io.InputStream;
import java.io.OutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * Helper methods for XML reading and writing.
 */
public class JAXBHelper {
    /**
     * Marshalls an object, and constructs a XML document and streams it to {@code os}.
     * @param o the object to be marshalled
     * @param os Output stream where the constructed XML will be outputed
     * @throws JAXBException if any error occurs while marshalling
     */
    public static void toXML(Object o, OutputStream os) throws JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(o.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(o, os);
        } catch(JAXBException e) {
            throw e;
        }
    }

    /**
     * Reads from {@code is} and marshalls an XML document to an object of type {@code T}.
     * @param clazz the class of the output object
     * @param is an input stream from which it will read the XML document
     * @param <T> The type of the output object
     * @return The constructed object
     * @throws JAXBException if any error occurs while marshalling
     */
    public static <T> T fromXML(Class<T> clazz, InputStream is) throws JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(is);
        } catch(JAXBException e) {
            throw e;
        }
    }

}
