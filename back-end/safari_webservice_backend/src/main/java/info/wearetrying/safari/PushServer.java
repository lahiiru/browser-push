package info.wearetrying.safari;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Version 1 Safari push notification backend API
 */
@WebServlet(name="server-servlet", urlPatterns = { "/v1/*" })
public class PushServer extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(PushServer.class);

	private void serve(HttpServletRequest req, HttpServletResponse resp)  {

		log.info(String.format("-------->| New request: { path:%s, time:%s, host:%s, ip:%s }",req.getPathInfo(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()),req.getServerName(), req.getRemoteAddr()));
		resp.setContentType("text/plain");

		// webServiceURL/version/pushPackages/websitePushID
		if(req.getPathInfo().contains("/pushPackages/")) {
			String site = req.getPathInfo().split("/pushPackages/")[1];
			log.info("<--------| Serving push package for websitePushID " + site);
			getZip(resp);

		// webServiceURL/version/devices/deviceToken/registrations/websitePushID
		} else if (req.getPathInfo().contains("/devices/") && req.getPathInfo().contains("/registrations/")) {
			String[] param = req.getPathInfo().split("(/devices/)|(/registrations/)");
			String deviceToken = param[0];
			String websitePushID = param[1];
			String requestMethod = req.getMethod();
			log.info(String.format("---------| Registration request received. %s %s %s",deviceToken, websitePushID, requestMethod));

		/*
		 * See https://developer.apple.com/library/content/documentation/NetworkingInternet/Conceptual/NotificationProgrammingGuideForWebsites/PushNotifications/PushNotifications.html#//apple_ref/doc/uid/TP40013225-CH3-SW17
		 * To troubleshoot by understanding log messages
		 */
		// webServiceURL/version/log
		} else if (req.getPathInfo().endsWith("/log")){
			try {
				log.info("---------| Log request received. "+readBody(req));
			} catch (Exception e){
				log.info("Error in logging request", e);
			}

		// webServiceURL/version/any-string
		} else {
			log.info("---------| Unkown request received.");
			try {
				resp.getWriter().write("BAD REQUEST");
			} catch (IOException e) {
				log.info("Error in the request", e);
			}
		}
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serve(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serve(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serve(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serve(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serve(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		serve(req, resp);
	}

	private String readBody(HttpServletRequest request) throws IOException {
		// Read from request
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	private void getZip(HttpServletResponse response){
		String fileName = "binaries/push-package.zip";
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition","attachment;filename=\"push-package.bin\"");
		FileInputStream is = null;
		try{
			File f = new File(fileName);
			int length = (int)f.length();
			byte[] arBytes = new byte[length];
			response.setContentLength(length);
			is = new FileInputStream(f);
			is.read(arBytes);
			is.close();
			ServletOutputStream op = response.getOutputStream();
			op.write(arBytes);
			op.flush();
		}catch(IOException ioe)
		{
			log.info("Error while loading" + fileName + "zip",ioe);
		}finally {
			try {
				if(is!=null){
					is.close();
				}
			} catch (IOException e) {
				log.info("Error while closing stream " + fileName + "zip", e);
			}
		}
	}
}
