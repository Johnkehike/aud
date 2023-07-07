package com.Auditionapp.Audition.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebFilter(urlPatterns = "/*", filterName = "WebSecurityConfig")
@Slf4j
@Component
public class WebSecurityConfig implements Filter {
    private static final int INACTIVITY_TIMEOUT = 10 * 60 * 1000; // 10 minutes in milliseconds


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, ServletException {

//        log.info("I'm here in the filter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            long lastAccessedTime = session.getLastAccessedTime();
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - lastAccessedTime;
            if (timeDifference > INACTIVITY_TIMEOUT) {
                log.info("Session Timeout");
//                session.setAttribute("sessionExpired", true);

                ((HttpServletResponse) response).sendRedirect("/web/sessionExpired");
                session.invalidate();

//                return;
            }
        }
        chain.doFilter(request, response);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}




