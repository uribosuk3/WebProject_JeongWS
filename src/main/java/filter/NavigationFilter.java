package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class NavigationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        Object loginUser = req.getSession().getAttribute("loginUser");

        req.setAttribute("isLogin", loginUser != null);
        req.setAttribute("loginUser", loginUser);

        chain.doFilter(request, response);
    }
}
