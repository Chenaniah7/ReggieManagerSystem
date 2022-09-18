package com.gcc.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.gcc.reggie.common.BaseContext;
import com.gcc.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1, 获取本次请求的url
        String requestUri = request.getRequestURI();

        log.info("本次请求为：" + requestUri);

        //定义不需要拦截的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "front/**",
                "/common/**"
        };

        boolean isCheck = check(urls,requestUri);

        //如果判断为不需要处理的请求，则直接放行
        if (isCheck){
            filterChain.doFilter(request,response);
            log.info("本次请求不需要处理："+requestUri);
            return;
        }

        //如果判断为已经登陆，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            log.info("用户已登录，用户id为："+request.getSession().getAttribute("employee"));
            return;
        }

        //如果未登录则返回到登陆界面
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    //2, 判断本次请求是否需要拦截处理
    public boolean check(String[] urls, String requestUri){
        for (String url : urls) {
            boolean isMatch = PATH_MATCHER.match(url,requestUri);
            if (isMatch){
                return true;
            }
        }
        return false;
    }
}
