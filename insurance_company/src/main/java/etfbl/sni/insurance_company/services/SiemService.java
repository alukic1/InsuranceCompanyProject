package etfbl.sni.insurance_company.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiemService {

    private static final Logger log = LoggerFactory.getLogger(SiemService.class);

    public void logSecurityEvent(String message) {
        log.info("[SIEM EVENT] {}", message);
    }

}