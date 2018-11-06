#set($dollar = '$')
package ${package}.gitb;

import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class used to hold and use the proxy configuration.
 */
@Component
public class ProxyInfo {

    @Value("${dollar}{proxy.enabled:false}")
    private boolean enabled;

    @Value("${dollar}{proxy.server:''}")
    private String server;

    @Value("${dollar}{proxy.port:-1}")
    private Integer port;

    @Value("${dollar}{proxy.type:'HTTP'}")
    private String type;

    @Value("${dollar}{proxy.auth.enabled:false}")
    private boolean authEnabled;

    @Value("${dollar}{proxy.auth.username:''}")
    private String username;

    @Value("${dollar}{proxy.auth.password:''}")
    private String password;

    @Value("${dollar}{proxy.nonProxyHosts:''}")
    private String nonProxyHosts;

    /**
     * Check to see if a proxy should be used.
     *
     * @return The check result.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Apply the proxy configuration to the given CXF HTTPConduit.
     *
     * @param httpConduit The conduit to process.
     */
    public void applyToCxfConduit(HTTPConduit httpConduit) {
        httpConduit.getClient().setProxyServer(server);
        httpConduit.getClient().setProxyServerPort(port);
        httpConduit.getClient().setProxyServerType(ProxyServerType.fromValue(type));
        httpConduit.getClient().setNonProxyHosts(nonProxyHosts);
        if (authEnabled) {
            if (httpConduit.getProxyAuthorization() == null) {
                httpConduit.setProxyAuthorization(new ProxyAuthorizationPolicy());
            }
            httpConduit.getProxyAuthorization().setUserName(username);
            httpConduit.getProxyAuthorization().setPassword(password);
        }
    }
}
