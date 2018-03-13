package ee.ria.eidas.client.demo.filter;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class EidasMetadataFilter extends GenericFilterBean {

    private final String externalService;

    public EidasMetadataFilter(String externalService) {
        this.externalService = externalService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpResponse samlServiceResponse = Request.Get(externalService + "/metadata").execute().returnResponse();

        if (samlServiceResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            ((HttpServletResponse)servletResponse).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);
            samlServiceResponse.getEntity().writeTo(servletResponse.getOutputStream());
        } else {
            throw new RuntimeException("Web service returned an error. " + samlServiceResponse.getStatusLine().getStatusCode());
        }

    }
}
