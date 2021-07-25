package cn.vtohru.web;

import cn.vtohru.annotation.Verticle;

import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Verticle
public class ResponseHandlerRegister {
    Map<MediaType, ResponseHandler> decodersByType = new LinkedHashMap<>(3);
    public ResponseHandlerRegister(Collection<ResponseHandler> responseHandlers) {
        if (responseHandlers != null) {
            for (ResponseHandler decoder : responseHandlers) {
                Collection<MediaType> mediaTypes = decoder.getMediaTypes();
                for (MediaType mediaType : mediaTypes) {
                    if (mediaType != null) {
                        decodersByType.put(mediaType, decoder);
                    }
                }
            }
        }
    }

    public Optional<ResponseHandler> findResponseHandler(MediaType mediaType) {
        if (mediaType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(decodersByType.get(mediaType));
    }
}
