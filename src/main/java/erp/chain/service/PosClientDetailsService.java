package erp.chain.service;

import com.saas.common.util.MD5Utils;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Xumx on 2015/5/29.
 */
@Service
@Primary
public class PosClientDetailsService extends BaseService implements ClientDetailsService {
    @Override
    public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
        ClientDetails clientDetails = null;

        List<Map<String, Object>> posList = select(String.format("select * from pos where access_token = '%s' and is_deleted = false", s));

        if (posList != null && posList.size() == 1) {
            Map<String, Object> pos = posList.get(0);
            String clientSecret = pos.get("password").toString() + pos.get("pos_code").toString() + pos.get("branch_code").toString() + pos.get("tenant_code").toString();
            clientSecret = MD5Utils.stringMD5(clientSecret);
            clientDetails = new BaseClientDetails(s, "SERVICE-ROUTER,POS-SERVICE", "USER", "client_credentials", s);
            ((BaseClientDetails) clientDetails).setClientSecret(clientSecret);
            ((BaseClientDetails) clientDetails).setAccessTokenValiditySeconds(1800);
        }

        if (clientDetails == null) {
            clientDetails = new BaseClientDetails();
        }

        return clientDetails;
    }
}
