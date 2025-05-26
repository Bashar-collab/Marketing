package com.custempmanag.marketing.permissionEvaluator.evaluator;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionHandlerRegistry handlerRegistry;

    public CustomPermissionEvaluator(PermissionHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if (permission.equals("EV")){
//            System.out.println("Condition is false");
            return true;
        }

        PermissionHandler handler = handlerRegistry.getHandler(targetType);
        if (handler == null) return false;

        return switch (permission.toString().toUpperCase()) {
            case "A0", "A1" -> handler.canAdd(auth);
            case "S0", "S1", "F0", "F1" -> handler.canView(auth, (Long) targetId);
            case "E0", "E1" -> handler.canEdit(auth, (Long) targetId);
            case "D0", "D1" -> handler.canDelete(auth, (Long) targetId);
            case "EV" -> handler.canView(auth, (Long) targetId)
                    && handler.canEdit(auth, (Long) targetId)
                    && handler.canDelete(auth, (Long) targetId)
                    && handler.canAdd(auth);
            default -> false;
        };
    }

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        return false; // Not used here
    }

}
