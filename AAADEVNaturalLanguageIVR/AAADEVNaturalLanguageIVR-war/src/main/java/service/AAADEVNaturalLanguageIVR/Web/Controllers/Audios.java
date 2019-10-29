/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Web.Controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.Arrays;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.util.logger.Logger;


/**
 *
 * @author umansilla
 */
@WebServlet(name = "Audios", urlPatterns = {"/Audios"})
public class Audios extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(getClass());
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try{
	        JSONObject json = new JSONObject();
	        setAccessControlHeaders(response);
	
	        BasicFileAttributes attrs;
	
	        response.setContentType("application/json");
	        HttpSession session = request.getSession(true);
	        Usuario user = (Usuario) session.getAttribute("userActive");
	        JSONObject jsonArrays = Arrays.arrayFiles(Constants.ROUTE_VERBIO_AUDIOS);
	        JSONArray jsonArrayObj = new JSONArray();
	        int counter = 0;
	        for (int i = 0; i < jsonArrays.length(); i++) {
	            String nameFile = jsonArrays.getString("Index" + i);
	            String[] arrOfStr = nameFile.split("_user");
	            String limitNameFile = arrOfStr[0] + "_user";
	            if (limitNameFile.equals(user.getVerbiouser())) {
	                JSONObject jsonAudioFile = new JSONObject();
	                File file = new File(Constants.ROUTE_VERBIO_AUDIOS + nameFile);
	                attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
	                FileTime time = attrs.creationTime();
	                String pattern = "yyyy-MM-dd HH:mm:ss";
	                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	                String formatted = simpleDateFormat.format(new Date(time.toMillis()));
	                String [] split = formatted.split(" ");
	                jsonAudioFile.put("Date", split[0]);
	                jsonAudioFile.put("Hour", split[1]);
	                jsonAudioFile.put("File", nameFile);
	                jsonArrayObj.put(jsonAudioFile);
	                counter++;
	
	            }
	        }
	        json.put("Total", counter);
	        json.put("Results", jsonArrayObj);
	        out.println(json);
		}catch(Exception e){
			logger.error("DoPost /Audios Error: " + e.toString());
			out.println(new JSONObject().put("status", "error"));
		}
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
    }

}
