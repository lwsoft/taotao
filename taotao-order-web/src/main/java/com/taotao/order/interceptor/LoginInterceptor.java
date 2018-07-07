package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.sso.service.UserService;
/**
 * 判读用户是否登录
 * @author 
 *
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TOKEN_KEY}")
	private String TOKEN_KEY;

	@Value("${SSO_URL}")
	private String SSO_URL;
	@Autowired
	private UserService userService;


	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 执行handler之前先执行此方法，拦截请求让用户登录就在这个方法拦截
		//1.从cookie中取token信息
		String token = CookieUtils.getCookieValue(request, TOKEN_KEY);
		//2.如果取不到token，跳转到sso的登录页面，需要把当前请求的url做为参数传递给sso，sso登录成功之后跳转回请求的页面
		if(StringUtils.isBlank(token)){
			//取当前请求的url
			String requestURL = request.getRequestURL().toString();
			//跳转到登录页面
			response.sendRedirect(SSO_URL + "/page/login?url=" + requestURL);
			return false;
		}
		
		//3.取到token，调用sso系统的服务判断用户是否登录
		TaotaoResult taotaoResult = userService.getUserByToken(token);
		//4.如果用户未登录，即没有取到用户信息，跳转到sso的登录页面
		if(taotaoResult.getStatus() != 200){
			//取当前请求的url
			String requestURL = request.getRequestURL().toString();
			//跳转到登录页面
			response.sendRedirect(SSO_URL + "/page/login?url=" + requestURL);
			return false;
		}
		
		//5.如果取到用户信息，就放行。
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// handler执行之后，modelAndView返回之前

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 在ModelAndView返回之后，异常处理
	}

}
