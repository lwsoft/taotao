package com.taotao.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class GlobalExceptioResolver implements HandlerExceptionResolver {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptioResolver.class);  

	@Override
	public ModelAndView resolveException(HttpServletRequest rquest, HttpServletResponse response, Object handler,
			Exception e) {
		logger.info("进入全局异常处理器。。。");
		logger.debug("测试handler的类型：" + handler.getClass());
		// 控制台打印异常
		e.printStackTrace();
		// 向日志文件中写入异常
		logger.error("系统发生异常", e);
		// 发邮件（采用jmail客户端进行发送）
		// 发短信
		// 展示错误页面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", "当前网络出现故障，请稍后重试！");
		// 返回逻辑视图，这样回去访问error目录下的error.jsp
		modelAndView.setViewName("error/exception");
		return modelAndView;

	}

}
