package cn.vtohru.web;


import javax.ws.rs.core.MediaType;
import java.util.List;

public interface ResponseHandler {

    List<MediaType> getMediaTypes();

}
