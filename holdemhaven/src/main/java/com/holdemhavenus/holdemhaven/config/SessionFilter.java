package com.holdemhavenus.holdemhaven.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
@Component
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();

        if (session.getAttribute("sessionId") == null) {
            String sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", UUID.randomUUID().toString());
            System.out.println("Session ID generated and stored: " + sessionId);
        }
        else {
            System.out.println("Existing Session ID: " +  session.getAttribute("sessionId"));
            System.out.println(session.getAttribute("username"));
            System.out.println(session.getAttribute("accountBalance"));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
