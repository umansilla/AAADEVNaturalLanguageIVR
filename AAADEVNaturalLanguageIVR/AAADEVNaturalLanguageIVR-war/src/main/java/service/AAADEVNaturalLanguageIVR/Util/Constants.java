package service.AAADEVNaturalLanguageIVR.Util;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class Constants {
	/*
	 * Attrubutes
	 */
	public static final String AUDIOS_FOLDER = "folder";
	public static final String IDIOMA_OPCION = "idioma";
	public static final String CLOUD_POVIDER = "cloudProvider";
	public static final String VPS_FQDN = "VPSFQDN";
	public static final String ROUTE_EXECUTION = "RouteExecution";
	public static final String AGENT_PHONE = "agentPhone";
	public static final String EMAIL_FROM = "emailFrom";
	public static final String EMAIL = "email";
	//Google Speech To Text
	public static final String GOOGLE_CLOUD_SPEECH_TO_TEXT = "GoogleCloudSpeechToText";
	//Verbio Voice Recognition
	public static final String SCORE_VOICE_RECOGNITION = "scorevoicerecognition";
	//IBM Watso Assistant
	public static final String IBM_WATSON_ASSISTANT_USER_NAME = "WAUserName";
	public static final String IBM_WATSON_ASSISTANT_WORK_SPACE_ID = "WAWorkSpaceId";
	public static final String IBM_WATSON_ASSISTANT_PASSWORD = "WAPassword";
	//IBM Text To Speecj
	public static final String IBM_TTS_USER_NAME = "IBMTTSUserName";
	public static final String IBM_TTS_PASSWORD = "IBMTTSPassword";
	//IBM Language Translator
	public static final String IBM_LANGUAGE_TRANSLATOR_USER_NAME = "IBMLanguageTranslatorUserName";
	public static final String IBM_LANGUAGE_TRANSLATOR_PASSWORD = "IBMLanguageTranslatorPassword";
	//IBM Natural Language Emotions
	public static final String IBM_NATURAL_LANGUAGE_USER_NAME = "IBMNaturalLanguageUserName";
	public static final String IBM_NATURAL_LANGUAGE_PASSWORD = "IBMNaturalLanguagePassword";
	
	//Security
	public final static String SECRET_KEY = "AmericasInternationalPoCDevelopmentTeam";
	public final static String SALT = "MexicoTeam";
	
	/*
	 * Paths
	 */
	public static final String PATH_TO_AUDIOS = getPathWebApp() + "/Audios/";
	public static final String PATH_TO_WEB_APP = getPathWebApp();
	private static String getPathWebApp() {
		String realPath = getApplcatonPath();
		String[] split = realPath.split("/");
		StringBuilder path = new StringBuilder();
		for (int k = 1; k < split.length - 1; k++) {
			path.append("/");
			path.append(split[k]);
		}
		return path.toString();
	}

	private static String getApplcatonPath() {
		CodeSource codeSource = Constants.class.getProtectionDomain()
				.getCodeSource();
		File rootPath = null;
		try {
			rootPath = new File(codeSource.getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			return e.toString();
		}
		return rootPath.getParentFile().getPath();
	}
	
	public static final String ROUTE_GRABACIONES_ES = "home/wsuser/web/Record/";
	public static final String ROUTE_GRABACIONES_PT = "home/wsuser/web/RecordPt/";
	public static final String ROUTE_GRABACIONES_EN = "home/wsuser/web/RecordEn/";
	public static final String ROUTE_INTENTS = "home/wsuser/web/Intent/";
	
	//VERBIO ROUTE
	public static final String ROUTE_VERBIO_AUDIOS = "home/wsuser/web/VerbioAudios/";
	
	//ACCESS FILE
	public static final String ROUTE_ACCESS_FILE = "home/wsuser/web/LogIn/Access.txt";
	//REGISTER FILE
	public static final String ROUTE_REGISTER_FILE = "home/wsuser/web/LogIn/Accesslogs.txt";
	//HTTP GET Access.txt
	public static final String ACCESS_FILE = "http://breeze2-132.collaboratory.avaya.com/services/AAADEVLOGGER/InputLogger/web/LogIn/Access.txt";
	
}
