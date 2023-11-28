package net.sf.javascribe.plugins.console.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConsoleServlet extends HttpServlet {

	public static final String CONSOLE_URI = "/console";
	
	/**
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uri = req.getRequestURI();
		String resourcePath = null;
		String contentType = null;

		if (CONSOLE_URI.equals(uri)) {
			resourcePath = "index.html";
			contentType = "text/html";
		} else if (uri.startsWith("/css/")) {
			resourcePath = "css/"+uri.substring(5);
			contentType = "text/css";
		} else if (uri.startsWith("/js/")) {
			resourcePath = "js/"+uri.substring(4);
			contentType = "text/javascript";
		}
		
		if (resourcePath!=null) {
			String basePath = "C:\\git\\javascribe\\system-plugins\\src\\main\\resources";
			FileInputStream in = new FileInputStream(basePath+"\\"+resourcePath);
			//InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
			
			if (in==null) {
				System.err.println("Couldn't get input stream for resource "+resourcePath);
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType(contentType);
				OutputStream out = resp.getOutputStream();
				while(in.available()>0) {
					out.write(in.read());
				}
				out.flush();
				out.close();
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
    }

}
