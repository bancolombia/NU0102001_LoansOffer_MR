package co.com.bancolombia.libreinversion.encoding;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.support.DefaultStrategiesHelper;
import reactor.core.Exceptions;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import java.util.Map;

public class Jaxb2SoapDecoder extends Jaxb2XmlDecoder {

    private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();

    public Jaxb2SoapDecoder() {
        super();
    }

    @Override
    public Object decode(DataBuffer dataBuffer, ResolvableType targetType,
                         @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        try {
            DefaultStrategiesHelper helper = new DefaultStrategiesHelper(WebServiceTemplate.class);
            WebServiceMessageFactory messageFactory = helper.getDefaultStrategy(WebServiceMessageFactory.class);
            WebServiceMessage message = messageFactory.createWebServiceMessage(dataBuffer.asInputStream());
            return unmarshal(message, targetType.toClass());
        } catch (Exception ex) {
            throw Exceptions.propagate(ex);
        } finally {
            DataBufferUtils.release(dataBuffer);
        }
    }

    private Object unmarshal(WebServiceMessage message, Class<?> outputClass) {
        try {
            Unmarshaller unmarshaller = initUnmarshallerPrivate(outputClass);
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(message.getPayloadSource(), outputClass);
            return jaxbElement.getValue();
        } catch (UnmarshalException ex) {
            throw new DecodingException("Could not unmarshal XML to " + outputClass, ex);
        } catch (JAXBException ex) {
            throw new CodecException("Invalid JAXB configuration", ex);
        }
    }

    private Unmarshaller initUnmarshallerPrivate(Class<?> outputClass) throws CodecException, JAXBException {
        Unmarshaller unmarshaller = this.jaxbContexts.createUnmarshaller(outputClass);
        return getUnmarshallerProcessor().apply(unmarshaller);
    }

}
