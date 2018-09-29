package com.atguigu.gmall.config;

import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.GetIpUtil;
import com.atguigu.gmall.util.HttpClientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);



        if (methodAnnotation == null){

            return  true;
        }
        boolean autoRedirect = methodAnnotation.autoRedirect();
        String token = "";

        String newToken = request.getParameter("token");

        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);

        if (StringUtils.isNotBlank(newToken)&&StringUtils.isBlank(oldToken)){

              token = newToken;

        }
        if (StringUtils.isBlank(newToken)&&StringUtils.isNotBlank(oldToken)){

                token = oldToken;
        }
        if (StringUtils.isNotBlank(newToken)&&StringUtils.isNotBlank(oldToken)){

                token = newToken;
        }

        if (StringUtils.isNotBlank(token)){



            String ip = GetIpUtil.getMyIpFormRequest(request);

            String url = "http://passport.gmall.com:8085/verify?token="+token+"&ip="+ip;

            String success = HttpClientUtil.doGet(url);

            if ("success".equals(success)){

                CookieUtil.setCookie(request,response,"oldToken",token,1000*60*60*24,true);

                Map map = JwtUtil.decode("atguigu0508", token, ip);

                request.setAttribute("userId",map.get("userId"));
                request.setAttribute("nickName",map.get("nickName"));
            }
            return true;
        }
        if (autoRedirect){

            return true;
        }else {

            response.sendRedirect("http://passport.gmall.com:8085/index?originUrl="+request.getRequestURL());
            return false;
        }

    }

}
