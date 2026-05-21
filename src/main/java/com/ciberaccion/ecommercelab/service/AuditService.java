package com.ciberaccion.ecommercelab.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ciberaccion.ecommercelab.entity.AuditLog;
import com.ciberaccion.ecommercelab.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    // REQUIRES_NEW — se guarda en su propia transacción
    // aunque la transacción del llamador haga rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String detail) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
