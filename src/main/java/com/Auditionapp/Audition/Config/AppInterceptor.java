package com.Auditionapp.Audition.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
@Slf4j
public class AppInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String requestURI = request.getRequestURI();

            //Do whatever you need
            if(request.getRequestURI().contains("/web")){
                HttpSession session = request.getSession();

                String user = (String) session.getAttribute("user");

                if (user == null) {

                    request.setAttribute("error", "Unauthorized Access, please login");
                    log.info("Redirecting");
                    response.sendRedirect("/home");
                    return false;
                }
            }


       return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("In the PostHandle method");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("In the afterCompletion method");
    }
}
