package in.ac.amu.zhcet.configuration;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.configuration.properties.SecureProperties;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecurePropertyConfig {

    private static Boolean PEPPER_SET;

    @Autowired
    SecurePropertyConfig(SecureProperties secureProperties) {
        String pepper = secureProperties.getPepper();
        if (!Strings.isNullOrEmpty(pepper) && !pepper.equals(SecurityUtils.getPepper())) {
            SecurityUtils.setPepper(pepper);
            log.info("Applied pepper to application");
            PEPPER_SET = true;
        } else {
            log.error("Using default pepper for app, this is dangerous and can lead to hacking into system");
            PEPPER_SET = false;
        }
    }

    public static Boolean isPepperSet() {
        return PEPPER_SET;
    }
}
