package br.com.spdealer.refatorado.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuditService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuditService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createAudit(Map<String, Object> payload) {
        String sql = "INSERT INTO audit_logs (event_type, user_id, details, created_at) VALUES (?,?,?,NOW())";
        String eventType = payload.getOrDefault("event_type", "UNKNOWN").toString();
        Object uidObj = payload.get("user_id");
        Integer userId = null;
        try {
            if (uidObj != null) {
                userId = Integer.parseInt(uidObj.toString());
            }
        } catch (NumberFormatException ex) {
            userId = null;
        }
        String details = payload.getOrDefault("details", payload.toString()).toString();
        jdbcTemplate.update(sql, eventType, userId, details);
    }
}
