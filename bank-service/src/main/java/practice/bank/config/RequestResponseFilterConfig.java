package practice.bank.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty(
        value="logging.enabled",
        havingValue = "true"
)
@Component
@Log4j2
public class RequestResponseFilterConfig implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        long start = System.currentTimeMillis();
        chain.doFilter(requestWrapper, responseWrapper);
        long end = System.currentTimeMillis();

        log.info("\n" +
                        "[REQUEST] {} - {} {} - {}\n" +
                        "Headers : {}\n" +
                        "Request : {}\n" +
                        "Response : {}\n",
                ((HttpServletRequest) request).getMethod(),
                ((HttpServletRequest) request).getRequestURI(),
                responseWrapper.getStatus(),
                (end - start) / 1000.0,
                getHeaders((HttpServletRequest) request),
                getRequestBody(requestWrapper),
                getResponseBody(responseWrapper));
    }

    private Map getHeaders(HttpServletRequest request) {
        Map headerMap = new HashMap<>();

        Enumeration headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = (String) headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                wrapper.copyBodyToResponse();
            }
        }
        return null == payload ? " - " : payload;
    }
}
