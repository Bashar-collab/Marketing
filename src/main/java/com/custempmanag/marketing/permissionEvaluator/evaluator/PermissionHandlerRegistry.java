package com.custempmanag.marketing.permissionEvaluator.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PermissionHandlerRegistry {
    private Map<String, PermissionHandler> handlers = new HashMap<>();

    @Autowired
    public PermissionHandlerRegistry(List<PermissionHandler> handlerBeans) {
        for (PermissionHandler handler : handlerBeans) {
            String name = handler.getClass().getSimpleName().replace("PermissionHandler", "");
            handlers.put(name, handler);
        }
    }

    public PermissionHandler getHandler(String type) {
        return handlers.get(type);
    }
}
