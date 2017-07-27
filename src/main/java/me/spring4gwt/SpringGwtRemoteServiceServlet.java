package me.spring4gwt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

@SuppressWarnings("serial")
public class SpringGwtRemoteServiceServlet extends RemoteServiceServlet {

    private static final Logger logger = LoggerFactory.getLogger(SpringGwtRemoteServiceServlet.class);

    private WebApplicationContext applicationContext;
    
    private PathResourceResolver pathResourceResolver = new PathResourceResolver();

    private List<Resource> resourceLocations;
    
    @Override
    public void init() {
        
        if (logger.isDebugEnabled()) {
            logger.debug("Spring GWT service exporter deployed");
        }

        applicationContext = getApplicationContext();

        ResourceProperties resourceProperties = applicationContext.getBean(ResourceProperties.class);

        String[] locationsStr = resourceProperties.getStaticLocations();

        resourceLocations = 
                Arrays.stream(locationsStr)
                        .map(applicationContext::getResource)
                        .collect(Collectors.toList());
    }

    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL,
                                                           String strongName) {
        // The request can tell you the path of the web app relative to the
        // container root.
        String contextPath = request.getContextPath();

        String modulePath = null;
        if (moduleBaseURL != null) {
            try {
                modulePath = new URL(moduleBaseURL).getPath();
            } catch (MalformedURLException ex) {
                // log the information, we will default
                log("Malformed moduleBaseURL: " + moduleBaseURL, ex);
            }
        }

        SerializationPolicy serializationPolicy = null;

        /*
         * Check that the module path must be in the same web app as the servlet
         * itself. If you need to implement a scheme different than this, override
         * this method.
         */
        if (modulePath == null || !modulePath.startsWith(contextPath)) {
            String message = "ERROR: The module path requested, "
                    + modulePath
                    + ", is not in the same web application as this servlet, "
                    + contextPath
                    + ".  Your module may not be properly configured or your client and server code maybe out of date.";
            log(message);
        } else {
            
            // Strip off the context path from the module base URL. It should be a
            // strict prefix.
            String contextRelativePath = modulePath.substring(contextPath.length());

            String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName(
                    contextRelativePath
                            + strongName);

            // Open the RPC resource file and read its contents.
            InputStream is = getWebResourceAsStream(serializationPolicyFilePath);

            if (is == null) {
                log("ERROR: The serialization policy file '"
                        + serializationPolicyFilePath
                        + "' was not found; did you forget to include it in this deployment?");
            } else {
                try {
                    serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
                } catch (ParseException e) {
                    log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
                } catch (IOException e) {
                    log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        return serializationPolicy;
    }

    private InputStream getWebResourceAsStream(String serializationPolicyFilePath) {
        
        Resource resource = pathResourceResolver.resolveResource(
                null, 
                serializationPolicyFilePath,
                resourceLocations, 
                null);

        if (resource != null) {
            try {
                return resource.getInputStream();
            } catch (IOException ignored) {
            }
        }
        
        return null;
    }

    @Override
    public String processCall(String payload) throws SerializationException {
        try {
            long t0 = System.currentTimeMillis();
            Object handler = getBean(getThreadLocalRequest());
            RPCRequest rpcRequest = RPC.decodeRequest(payload, handler.getClass(), this);
            onAfterRequestDeserialized(rpcRequest);
            if (logger.isDebugEnabled()) {
                logger.debug(
                        rpcRequest.getMethod().getDeclaringClass().getSimpleName() + "." + rpcRequest.getMethod()
                                .getName() + ",begin");
            }
            String resp = RPC.invokeAndEncodeResponse(handler, rpcRequest.getMethod(), rpcRequest.getParameters(),
                    rpcRequest
                            .getSerializationPolicy());
            if (logger.isDebugEnabled()) {
                logger.debug(
                        rpcRequest.getMethod().getDeclaringClass().getSimpleName() + "." + rpcRequest.getMethod()
                                .getName() + ",end," + (System.currentTimeMillis() - t0));
            }
            return resp;
        } catch (IncompatibleRemoteServiceException ex) {
            log("An IncompatibleRemoteServiceException was thrown while processing this call.", ex);
            return RPC.encodeResponseForFailure(null, ex);
        }
    }

    /**
     * Determine Spring bean to handle request based on request URL, e.g. a
     * request ending in /myService will be handled by bean with name
     * "myService".
     *
     * @param request request
     * @return handler bean
     */
    private Object getBean(HttpServletRequest request) {
        String service = getService(request);
        Object bean = applicationContext.getBean(service);
        if (!(bean instanceof RemoteService)) {
            throw new IllegalArgumentException(
                    "Spring bean is not a GWT RemoteService: " + service + " (" + bean + ")");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Bean for service " + service + " is " + bean);
        }
        return bean;
    }

    /**
     * Parse the service name from the request URL.
     *
     * @param request request
     * @return bean name
     */
    private String getService(HttpServletRequest request) {
        String url = request.getRequestURI();
        String service = url.substring(url.lastIndexOf("/") + 1);
        if (logger.isDebugEnabled()) {
            logger.debug("Service for URL " + url + " is " + service);
        }
        return service;
    }

    private WebApplicationContext getApplicationContext() {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (applicationContext == null) {
            throw new IllegalStateException("No Spring web application context found");
        }
        return applicationContext;
    }
}
