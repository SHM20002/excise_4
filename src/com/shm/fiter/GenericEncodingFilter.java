package com.shm.fiter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 拓薪教育：樱木老师
 * 腾讯课堂搜索：拓薪教育
 * 腾讯课堂直播间地址：https://ke.qq.com/course/149432
 * 加入QQ群领取免费资料和视频：QQ群号 344379612
 *
 * 解决post和get中文乱码问题的过滤器
 */
public class GenericEncodingFilter implements Filter {

	@Override
	public void destroy() {
	}

	/**
	 * 拦截方法
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
		// 转型为与协议相关对象
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		// 对request包装增强
		HttpServletRequest myrequest = new MyRequest(httpServletRequest);
		// 放行
		chain.doFilter(myrequest, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

}

// 自定义request对象
class MyRequest extends HttpServletRequestWrapper {

	private HttpServletRequest request;

	private boolean hasEncode;

	public MyRequest(HttpServletRequest request) {
		super(request);// super必须写
		this.request = request;
	}

	// 对需要增强方法 进行覆盖
	@Override
	public Map getParameterMap() {
		// 先获得请求方式
		String method = request.getMethod();
		if (method.equalsIgnoreCase("post")) {
			// post请求
			try {
				// 处理post乱码
				request.setCharacterEncoding("utf-8");
				return request.getParameterMap();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (method.equalsIgnoreCase("get")) {
			// get请求
			Map<String, String[]> parameterMap = request.getParameterMap();
			if (!hasEncode) { // 确保get手动编码逻辑只运行一次
				for (String parameterName : parameterMap.keySet()) {
					String[] values = parameterMap.get(parameterName);
					if (values != null) {
						for (int i = 0; i < values.length; i++) {
							try {
								// 处理get乱码
								values[i] = new String(values[i]
										.getBytes("ISO-8859-1"), "utf-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					}
				}
				hasEncode = true;
			}
			return parameterMap;
		}

		return super.getParameterMap();
	}

	@Override
	public String getParameter(String name) {
		Map<String, String[]> parameterMap = getParameterMap();
		String[] values = parameterMap.get(name);
		if (values == null) {
			return null;
		}
		return values[0]; // 取回参数的第一个值
	}

	@Override
	public String[] getParameterValues(String name) {
		Map<String, String[]> parameterMap = getParameterMap();
		String[] values = parameterMap.get(name);
		return values;
	}

}