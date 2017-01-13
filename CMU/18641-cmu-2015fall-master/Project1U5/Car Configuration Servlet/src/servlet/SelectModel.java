package servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import client.SocketClient;
import util.ServletUtil;

/**
 * Servlet implementation class SelectModel
 */
@WebServlet("/SelectModel")
public class SelectModel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SocketClient client;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SelectModel() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    /**
	 * @see HttpServlet#HttpServlet() init the servlet
	 */
	@Override
	public void init(ServletConfig config) {
		String strLocalHost = "";
		// get local IP address
		try {
			strLocalHost = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.err.println("Unable to find local host");
		}
		// start this client
		this.client = new SocketClient(strLocalHost);
		client.openConnection();
	}
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		client.closeSession();
		super.destroy();
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// wait the client to open connection to server
		while (!client.isOpen()) {

		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		// get interaction from and to server from SocketClient thread instance
		ObjectOutputStream objectOutputStream = client.getObjectOutputStream();
		ObjectInputStream objectInputStream = client.getObjectInputStream();

		// write operation 3 in server and start a dialog for getting a list of car
		objectOutputStream.writeObject("3");
		objectOutputStream.flush();
		
		String fromServer = "";
		try {
			fromServer = (String) objectInputStream.readObject();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Server: " + fromServer);
		
		// get model list from stream
		ArrayList<String> modelList = null;
		try {
			modelList = (ArrayList<String>) objectInputStream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		// handle the thread issue
		if (modelList == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
		String title = "All available models";

		out.println(ServletUtil.headWithTitle(title) + "<BODY>\n" + "<H1 ALIGN=\"CENTER\">"
				+ title + "</H1>\n");

		// if there is at least one car in server, start if
		// else start a page without option
		if (modelList.size() != 0) {
			out.println("<form ALIGN=\"CENTER\" action=\"SelectOption\" method=\"Get\">");
			out.println("<p>" + "Models:");

			// print select list in page
			out.println("<select name = \"model\">");
			for (int i = 0; i < modelList.size(); i++) {
				out.println("<option value=\"" + modelList.get(i) + "\">" + modelList.get(i) + "</option>");
			}
			out.println("</select>");
			out.println("<p>");
			out.println("<input type=\"submit\" value=\"Done\">");
		} else {
			out.println("No model available!");
		}
		out.println("</form ></BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
