package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Config;
import data.Database;

@WebServlet("/AddUser")
public class AddUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Database db;
		
		//From previous page, extract parameters
		String username = request.getParameter("username");
		String pass = request.getParameter("pass");
		
		//Set up variables to hold response
		boolean success = true;
		String errorMsg = "";
		
		//Check for empty input
		if (pass.length() == 0) {
			success = false;
			errorMsg += "The password is empty! ";
			pass = "";
		}
		
		if (username.length() == 0) {
			success = false;
			errorMsg += "The username is empty! ";
			username = "";
		}
		
		String hashPass = "";
		//Hash using sha256
		try {
	        MessageDigest md = MessageDigest.getInstance(Config.hashAlgo);
	        byte[] hashInBytes = md.digest(pass.getBytes(StandardCharsets.UTF_8));
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hashInBytes) {
	        	sb.append(String.format("%02x", b));
	        }
	        hashPass = sb.toString();
		} catch (NoSuchAlgorithmException e) { 
			e.printStackTrace(); 
		}
		//Begin database access
		ResultSet rs = null;
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		//If we didn't have null input, go into main database access
		if (success) {
			try {
				success = false;
				
				db = new Database();
				
				rs = db.getUserfromUsers(username);
				
				if (rs.next()) {
					//If a user with that username exists
					success = false;
					errorMsg = "A user with that username already exists!";
				}
				else {
					//Insert the new user 
					success = true;
					db.insertUserintoUsers(username, hashPass);					
				}
				//Set up a JSON return
				String objectToReturn =
						  "{\n"
							+ "\"success\": \"" + success + "\",\n"
							+ "\"data\": {\n"
								+ "\"errorMsg\": \"" + errorMsg + "\",\n"
								+ "\"username\": \"" + username + "\"\n"
							+ "}\n" 
						+ "}";
				out.print(objectToReturn);
			} catch(SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			} catch(ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} 
		}
		else {
			String objectToReturn =
					  "{\n"
							+ "\"success\": \"" + success + "\",\n"
							+ "\"data\": {\n"
								+ "\"errorMsg\": \"" + errorMsg + "\",\n"
								+ "\"username\": \"" + username + "\"\n"
							+ "}\n" 
						+ "}";
			out.print(objectToReturn);
		}
		
	}
}


