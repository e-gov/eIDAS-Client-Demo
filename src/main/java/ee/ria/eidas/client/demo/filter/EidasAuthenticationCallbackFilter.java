package ee.ria.eidas.client.demo.filter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import static org.apache.http.client.fluent.Form.*;

public class EidasAuthenticationCallbackFilter extends AbstractAuthenticationProcessingFilter {

    private final String externalService;

    public EidasAuthenticationCallbackFilter(String defaultFilterProcessesUrl, String externalService) {
        super(defaultFilterProcessesUrl);
        this.externalService = externalService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {

        try {
            HttpResponse response = Request.Post(externalService + "/returnUrl").bodyForm(form()
                    .add("SAMLResponse", ServletRequestUtils.getRequiredStringParameter(httpServletRequest, "SAMLResponse"))
                    .add("RelayState", ServletRequestUtils.getStringParameter(httpServletRequest, "RelayState")).build()
            ).execute().returnResponse();

            logger.debug("Response received: " + EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new InternalAuthenticationServiceException("Something went wrong! Authentication service returned an error: " + response.getStatusLine().getStatusCode());
            }

            Map<String, Object> result = JsonParserFactory.getJsonParser().parseMap(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            Map<String, Map<String, Object>> attributes = (Map<String, Map<String, Object>>) result.get("attributes");

            return new PreAuthenticatedAuthenticationToken(attributes.get("FirstName") + " " + attributes.get("FamilyName"), attributes.get("PersonIdentifier"), new ArrayList<>());

        } catch (IOException | ServletRequestBindingException e) {
            throw new InternalAuthenticationServiceException("Something went wrong! Invalid incoming request!", e);
        }
    }
}
