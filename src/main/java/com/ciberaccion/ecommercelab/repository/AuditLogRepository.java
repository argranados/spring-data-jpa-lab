package com.ciberaccion.ecommercelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciberaccion.ecommercelab.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
