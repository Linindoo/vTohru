package cn.vtohru.web;

import cn.vtohru.annotation.GlobalScope;
import cn.vtohru.annotation.Verticle;
import cn.vtohru.context.VerticleApplicationContext;
import io.micronaut.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Verticle
@GlobalScope
public class ErrorHandlerRegister {
    Map<Integer, ErrorHandler> errorHandlerMap = new HashMap<>();

    public ErrorHandlerRegister(ApplicationContext context, List<ErrorHandler> errorHandlers) {
        VerticleApplicationContext vcontext = (VerticleApplicationContext) context;
        if (errorHandlers != null) {
            for (ErrorHandler errorHandler : errorHandlers) {
                if (vcontext.isScoped(vcontext.getBeanDefinition(errorHandler.getClass()))) {
                    if (!errorHandlerMap.containsKey(errorHandler.getCode())) {
                        errorHandlerMap.put(errorHandler.getCode(), errorHandler);
                    }
                }
            }
        }
    }

    public Map<Integer, ErrorHandler> getErrorHanderMap() {
        return errorHandlerMap;
    }
}
