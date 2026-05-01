package br.com.spdealer.refatorado.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuditController {

    private final AuditService auditService;

    @Autowired
    public AdminAuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/audit")
    public ResponseEntity<?> createAudit(@RequestBody Map<String, Object> payload) {
        auditService.createAudit(payload);
        return ResponseEntity.ok().build();
    }
}
