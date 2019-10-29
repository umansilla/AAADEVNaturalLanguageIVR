package service.AAADEVNaturalLanguageIVR.Web.Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.avaya.collaboration.util.logger.Logger;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Util.PartToString;
import service.AAADEVNaturalLanguageIVR.Web.Actions.ChangeLanguage;
import service.AAADEVNaturalLanguageIVR.Web.Actions.CloseSession;
import service.AAADEVNaturalLanguageIVR.Web.Actions.DeleteAudios;
import service.AAADEVNaturalLanguageIVR.Web.Actions.GetAllUsers;
import service.AAADEVNaturalLanguageIVR.Web.Actions.GetAudiosAction;
import service.AAADEVNaturalLanguageIVR.Web.Actions.GetEmotionsAction;
import service.AAADEVNaturalLanguageIVR.Web.Actions.RestartTimeSession;


/**
 *
 * @author umansilla
 */
@MultipartConfig
@WebServlet(name = "ControlPad", urlPatterns = {"/ControlPad"})
public class ControlPad extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(getClass());
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession userSession = (HttpSession) request.getSession();
        Usuario usuario = (Usuario) userSession.getAttribute("userActive");
        if(usuario == null){
        	request.getRequestDispatcher("LogIn/LogIn.html").forward(request, response);
        }else{
	        String pageHome = request.getParameter("Location");
	        if(pageHome == null || pageHome.isEmpty()){
	        	pageHome = "NLU";
	        }
	        switch (pageHome) {
			case "home":
				request.setAttribute("Usuario", usuario);
	            request.getRequestDispatcher("NaturalLanguage/Home.jsp").forward(request, response);
				break;
			case "NLU":
				 GetAudiosAction audios = new GetAudiosAction();
	             request.setAttribute("Registros", audios.getAudios(usuario.getLanguage()));
	             usuario.setPassword(null);
	             request.setAttribute("Usuario", usuario);
	             request.getRequestDispatcher("NaturalLanguage/index.jsp").forward(request, response);
				break;
			case "User":
				request.setAttribute("Usuario", usuario);
				request.getRequestDispatcher("UserSettings/User.jsp").forward(request, response);
				break;
			case "AudiosControl":
				request.setAttribute("Usuario", usuario);
				request.getRequestDispatcher("AudioControls/AudioControl.jsp").forward(request, response);
				break;
			case "AdminPage":
				if(usuario.getAdmin().equals("YES")){
					request.setAttribute("Usuario", usuario);
					request.setAttribute("ListUsuarios", new GetAllUsers().getAllUsers());
					request.getRequestDispatcher("Administration/AdministratorPage.jsp").forward(request, response);
				}else{
					request.getRequestDispatcher("PageNotFound/index.html").forward(request, response);
				}
				break;
			default:
				break;
			}
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	logger.info("doPost");
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        String action = new PartToString().getStringValue(request.getPart("action"));
        try{
	        switch (action) {
	            case "GetEmotions":
	                GetEmotionsAction actionEmotions = new GetEmotionsAction(request);
	                json = actionEmotions.getEmotions();
	                break;
	            case "DeleteAudios":
	                DeleteAudios audio = new DeleteAudios(request);
	                json = audio.delete();
	                break;
	            case "CloseSession":
	                CloseSession session = new CloseSession(request);
	                json = session.closeSession();
	                break;
	            case "ChangeLanguage":
	                ChangeLanguage language = new ChangeLanguage(request);
	                json = language.changeLanguage();
	                break;
	            case "RestartTimeSession":
	                RestartTimeSession sessiontime = new RestartTimeSession(request);
	                json = sessiontime.restartTime();
	                break;
	        }
	        out.println(json);
        }catch(Exception e){
        	logger.error("Error DoPost: " + e.toString());
        	out.println(new JSONObject().put("status", "error"));
        }
        
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Accept, X-Requested-With");
    }
}
