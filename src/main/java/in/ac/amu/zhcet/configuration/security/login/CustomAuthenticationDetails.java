package in.ac.amu.zhcet.configuration.security.login;

import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class CustomAuthenticationDetails extends WebAuthenticationDetails {
    private String remoteAddress;

    public CustomAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.remoteAddress = Utils.getClientIP(request);
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }
}