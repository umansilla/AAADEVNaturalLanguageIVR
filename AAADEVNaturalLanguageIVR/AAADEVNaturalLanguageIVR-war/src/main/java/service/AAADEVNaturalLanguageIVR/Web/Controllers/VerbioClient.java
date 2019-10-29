/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Web.Controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GetFileAccess;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
import service.AAADEVNaturalLanguageIVR.Web.Actions.VerbioClientRequest;

import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;
/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "VerbioClient", urlPatterns = {"/VerbioClient"})
public class VerbioClient extends HttpServlet {

	private final Logger logger = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        String requestVerbiotString = new PartToString().getStringValue(request.getPart("request"));
        response.setContentType("application/json");
        HttpSession session = request.getSession(true);
        Usuario user = (Usuario) session.getAttribute("userActive");

        if (requestVerbiotString.equals("VERIFY")) {
            String base64AudioString = new PartToString().getStringValue(request.getPart("base64Audio"));
            
            try {
                json = verbioVerify(base64AudioString, user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }
        if (requestVerbiotString.equals("ADD_FILE")) {

            String base64AudioString = new PartToString().getStringValue(request.getPart("base64Audio"));
            try {
                json = verbioAddFile(base64AudioString, user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }
        if (requestVerbiotString.equals("TRAIN")) {
            try {
                json = verbioTrain(user.getVerbiouser());
                createTrain(user);
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }
        if (requestVerbiotString.equals("USER_INFO")) {
            try {
                json = verbioUserInfo(user.getVerbiouser());
                out.println(json);
            } catch (Exception e) {
                logger.info("VerbioClient Error: " + e.toString());
                json.put("error", "error");
                out.println(json);
            }
        }

    }

    public JSONObject verbioVerify(String base64Audio, String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"" + base64Audio + "\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"VERIFY\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"0\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";
        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;

    }

    public JSONObject verbioAddFile(String base64Audio, String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"" + base64Audio + "\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"ADD_FILE\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"0\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";

        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;
    }

    public JSONObject verbioTrain(String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"TRAIN\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";

        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;
    }

    public JSONObject verbioUserInfo(String userVerbio) throws IOException, SSLUtilityException {

        String payload = "{\n"
                + "	\"user_data\":\n"
                + "	{\n"
                + "		\"filename\":\"\",\n"
                + "		\"username\": \"" + userVerbio + "\",\n"
                + "		\"action\": \"USER_INFO\",\n"
                + "		\"score\": \"\",\n"
                + "		\"spoof\": \"0\",\n"
                + "		\"grammar\": \"\",\n"
                + "		\"lang\": \"\"\n"
                + "	}\n"
                + "}";
        VerbioClientRequest request = new VerbioClientRequest();
        JSONObject jsonResponse = request.makeRequest(payload);
        return jsonResponse;
    }
    
    public void createTrain(Usuario user) throws IOException {
        String jsonData = new GetFileAccess().fileHttp();
        JSONArray jobj = new JSONArray(jsonData);
        for (int i = 0; i < jobj.length(); i++) {
            String userName = jobj.getJSONObject(i).getString("username");
            if (userName.equals(user.getUsername())) {
                JSONObject jsonAccount = jobj.getJSONObject(i);
                jsonAccount.put("train", "yes");
            } else {
                continue;
            }
        }
        FileWriter fichero = new FileWriter(Constants.ROUTE_ACCESS_FILE);
        PrintWriter pw = new PrintWriter(fichero);
        pw.println(jobj);
        fichero.close();

    }
}
