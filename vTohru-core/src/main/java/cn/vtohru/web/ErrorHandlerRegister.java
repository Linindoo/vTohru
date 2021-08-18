package cn.vtohru.web;

import cn.vtohru.annotation.Verticle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Verticle
public class ErrorHandlerRegister {
    Map<Integer, ErrorHandler> errorHandlerMap = new HashMap<>();

    public ErrorHandlerRegister(List<ErrorHandler> errorHandlers) {
        if (errorHandlers != null) {
            for (ErrorHandler errorHandler : errorHandlers) {
                if (!errorHandlerMap.containsKey(errorHandler.getCode())) {
                    errorHandlerMap.put(errorHandler.getCode(), errorHandler);
                }
            }
        }
    }

    public Map<Integer, ErrorHandler> getErrorHanderMap() {
        return errorHandlerMap;
    }
}
