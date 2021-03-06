package cn.vtohru.web;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;

import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Verticle
@GlobalScope
public class ResponseHandlerRegister {
    Map<MediaType, AbstractResponseHandler> decodersByType = new LinkedHashMap<>(3);

    public ResponseHandlerRegister(ApplicationContext context, Collection<AbstractResponseHandler> responseHandlers) {
        if (responseHandlers != null) {
            VerticleApplicationContext vContext = (VerticleApplicationContext) context;
            for (AbstractResponseHandler decoder : responseHandlers) {
                if (vContext.isScoped(vContext.getBeanDefinition(decoder.getClass()))) {
                    Collection<MediaType> mediaTypes = decoder.getMediaTypes();
                    for (MediaType mediaType : mediaTypes) {
                        if (mediaType != null) {
                            decodersByType.put(mediaType, decoder);
                        }
                    }
                }
            }
        }
    }

    public Optional<AbstractResponseHandler> findResponseHandler(MediaType mediaType) {
        if (mediaType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(decodersByType.get(mediaType));
    }
}
