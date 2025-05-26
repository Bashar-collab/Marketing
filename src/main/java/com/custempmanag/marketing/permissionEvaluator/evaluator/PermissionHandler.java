package com.custempmanag.marketing.permissionEvaluator.evaluator;

import org.springframework.security.core.Authentication;

public interface PermissionHandler {
    boolean canAdd(Authentication authentication);
    boolean canView(Authentication authentication, Long targetId);
    boolean canEdit(Authentication authentication, Long targetId);
    boolean canDelete(Authentication authentication, Long targetId);
}
