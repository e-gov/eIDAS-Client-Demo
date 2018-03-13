package ee.ria.eidas.client.demo.filter;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EidasAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final String externalService;

    public EidasAuthenticationFilter(String defaultFilterProcessesUrl, String externalService) {
        super(defaultFilterProcessesUrl);
        this.externalService = externalService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        try {
            UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(externalService + "/login");
            uri.queryParam("country", ServletRequestUtils.getStringParameter(httpServletRequest, "country"));
            uri.queryParam("loa", ServletRequestUtils.getStringParameter(httpServletRequest, "loa"));
            uri.queryParam("relayState", ServletRequestUtils.getStringParameter(httpServletRequest, "relayState"));

            HttpResponse response = Request.Post(uri.build().toUriString()).execute().returnResponse();
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new InternalAuthenticationServiceException("Web service returned an error. " + response.getStatusLine().getStatusCode());
            }

            httpServletResponse.setStatus(HttpStatus.SC_OK);
            httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            response.getEntity().writeTo(httpServletResponse.getOutputStream());
            return null;
        } catch (ServletRequestBindingException e) {
            throw new InternalAuthenticationServiceException("Invalid request parameters!", e);
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException("Web service not available!", e);
        }
    }
}
