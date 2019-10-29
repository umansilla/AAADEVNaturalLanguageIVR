package service.AAADEVNaturalLanguageIVR.Http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayNoUserProfile;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayRecordNumeroDeCuenta;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayUsuarioNoEntrenado;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayVerbioVerify;
import service.AAADEVNaturalLanguageIVR.Util.AttributeStore;
import service.AAADEVNaturalLanguageIVR.Util.Constants;
import service.AAADEVNaturalLanguageIVR.Util.RegisterInputs;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaService;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase que se usa para identificar el numero de cuenta grabado por voz.
 * @author umansilla
 *
 */
public class SpeechToTextNumeroDeCuenta {
	private static final Logger logger = Logger.getLogger(SpeechToTextNumeroDeCuenta.class);
	private final Call call;
	private Usuario usuario;
	private static int numeroDeOportunidadesRecord;
	/**
	 * Constructor de la clase SpeechToTextNumeroDeCuenta.
	 * @param call Objeto Call del SDK Avaya Breeze.
	 */
	public SpeechToTextNumeroDeCuenta(final Call call){
		this.call = call;
	}
	
	/**
	 * Metodo creado para realizar conversion de voz a texto y obtener el numero de cuenta dictado.
	 * @param participant Numero que esta llamando al IVR
	 * @param requestid Request Id que que se obtiene al iniciar la reproduccion del audio Helsinky.wav
	 * @param mediaServicehelsinky Media service del audio de Helsinky.wav que se esta reproducioendo.
	 * @throws ClientProtocolException Error que se lanza al existir un error en la peticion HTTPS.
	 * @throws IOException Error que se lanza al no existir respuesta en la peticion HTTPS.
	 * @throws SSLUtilityException Error que se lanza al existir un error en certificados por una peticion HTTPS.
	 */
	public void speechToText(Participant participant, UUID requestid, MediaService mediaServicehelsinky) throws ClientProtocolException, IOException, SSLUtilityException{
		/*
		 * Petición a Google Cloud
		 */
		String numeroDeCuenta = null;
		StringBuilder numeroDeCuentaStr = new StringBuilder();
		if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.ROUTE_EXECUTION).equals("VPS")){
		VPSRquest vpsPost = new VPSRquest(call);
		String[] arregloGoogle = vpsPost.vpsPOST();
		numeroDeCuenta = arregloGoogle[0];
		String confidence = arregloGoogle[1];
		logger.fine("Confianza: " + confidence);
		String[] arrOfNumCuenta = numeroDeCuenta.split(" ");
	        for (int i = 0; i <= arrOfNumCuenta.length - 1; i++) {
	            numeroDeCuentaStr.append(arrOfNumCuenta[i]);
	        }
        logger.fine("Numero de cuenta: " + numeroDeCuentaStr.toString());
		}
		
		if(AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.ROUTE_EXECUTION).equals("Breeze")){
			GoogleRequest request = new GoogleRequest(call);
			String responseGoogle = request.googleRequestSTT();
	        JSONObject jsonResponse = new JSONObject(responseGoogle);
	        JSONArray jsonArray =  jsonResponse.getJSONArray("results");
	        JSONObject jsonObject = jsonArray.getJSONObject(0);
	        JSONArray jsonArrayAlternatives = jsonObject.getJSONArray("alternatives");
	        String transcript = jsonArrayAlternatives.getJSONObject(0).getString("transcript");
	        float confidenceFloat = jsonArrayAlternatives.getJSONObject(0).getFloat("confidence");
	        logger.info("Confianza: "  + Float.toString(confidenceFloat));
	        numeroDeCuenta = transcript;
	        String[] arrOfNumCuenta = numeroDeCuenta.split(" ");
		        for (int i = 0; i <= arrOfNumCuenta.length - 1; i++) {
		            numeroDeCuentaStr.append(arrOfNumCuenta[i]);
		        }
	        logger.fine("Numero de cuenta " + numeroDeCuentaStr.toString());
		}
				//RECUPERAR EL NUMERO DE CUENTA Y DESPUES VALIDACION POR VERBIO.
				Boolean existNumeroDeCuenta = false;
				Boolean trainedUser = false;
				String jsonData = null;
				GetFileAccess get = new GetFileAccess();
				jsonData = get.fileHttp();
				JSONArray jobj = new JSONArray(jsonData);
				for (int i = 0; i < jobj.length(); i++){
					if(jobj.getJSONObject(i).has("Caja_Social")){
						JSONObject cajaSocial = jobj.getJSONObject(i).getJSONObject("Caja_Social");
						String cuentaCajaSocial = cajaSocial.getString("Cuenta_Caja_Social");
						if(numeroDeCuentaStr.toString().equals(cuentaCajaSocial)){
							existNumeroDeCuenta = true;
							String train = jobj.getJSONObject(i).has("train")?jobj.getJSONObject(i).getString("train"):"no";
							if (train.equals("yes")){
								String userName = jobj.getJSONObject(i).has("username")?jobj.getJSONObject(i).getString("username"):"";
				                String name = jobj.getJSONObject(i).has("name") ? jobj.getJSONObject(i).getString("name") : "";
				                String verbiouser = jobj.getJSONObject(i).has("verbiouser") ? jobj.getJSONObject(i).getString("verbiouser") : "";
				                String fecha = jobj.getJSONObject(i).has("fecha") ? jobj.getJSONObject(i).getString("fecha") : "";
				                String hora = jobj.getJSONObject(i).has("hora") ? jobj.getJSONObject(i).getString("hora") : "";
				                String phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "";
				                train = jobj.getJSONObject(i).has("train") ? jobj.getJSONObject(i).getString("train") : "";
				                String country = jobj.getJSONObject(i).has("country") ? jobj.getJSONObject(i).getString("country") : "";
				                //MODIFICADO EL 10 de Julio 2019
				                Boolean cajaSocialExists = jobj.getJSONObject(i).has("Caja_Social")?true:false;
				                String cuenta = "";
				                String saldo = "";
				                ArrayList<String> historicoList = null;
				                if(cajaSocialExists){
				                    cuenta = cajaSocial.getString("Cuenta_Caja_Social");
				                    saldo = cajaSocial.getString("Saldo_Caja_Social");
				                    JSONArray cajaSocialArray = cajaSocial.getJSONArray("Historico_Caja_Social");
				                    historicoList = new ArrayList<String>();
				                    for (int j = 0; j <= cajaSocialArray.length() - 1; j++) {
				                        historicoList.add(cajaSocialArray.getString(j));
				                    }
				                }
				                usuario = new Usuario(jobj.getJSONObject(i).getInt("id"), name, verbiouser, userName, fecha, hora, phone, train, country, cuenta, historicoList, saldo, true);										
								trainedUser = true;
							}
							break;
						}else{
							continue;
						}
					}else{
						continue;
					}
				}
				try{
					if(existNumeroDeCuenta == true && trainedUser == true){
						//EXISTE EL NÚMERO DE CUENTA, CONTNUA PARA VALIDACIÓN POR VERBIO.
						logger.fine("EL NUMERO DE CUENTA SI EXISTE Y EL USUARIO ESTA ENTRENADO EN VERBIO");
						if(getNumeroDeOportunidadesRecord() == 1){
							setNumeroDeOportunidadesRecord(0);
						}
						new RegisterInputs().registerUserIVR(usuario, call.getCallingParty().getHandle());
						PlayVerbioVerify play = new PlayVerbioVerify(call, usuario);
						play.verify();
					}
					if(existNumeroDeCuenta == false && trainedUser == true){
						//NO EXISTE EL NUMERO DE CUENTA
						logger.info("NO EXISTE EL NUMERO DE CUENTA, SOLICITA GRABAR VOZ NUEVAMENTE");
						if(getNumeroDeOportunidadesRecord() == 0){
							logger.info("PRIMERA OPORTUNIDAD");
							setNumeroDeOportunidadesRecord(1);
							new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
							PlayRecordNumeroDeCuenta play = new PlayRecordNumeroDeCuenta(call);
							play.playRecordAnnouncement();							
						}else{
							logger.info("SEGUNDA OPORTUNIDAD");
							setNumeroDeOportunidadesRecord(0);
							new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
							new PlayNoUserProfile(call).noUser();
						}
					}
					if(existNumeroDeCuenta == true && trainedUser == false){
						//EL USUARIO NO ESTA ENTRENADO (DROP CALL).
						logger.error("EL USUARIO NO ESTA ENTRENADO");
						if(getNumeroDeOportunidadesRecord() == 0){
							logger.info("PRIMERA OPORTUNIDAD");
							setNumeroDeOportunidadesRecord(1);
							new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
							PlayRecordNumeroDeCuenta play = new PlayRecordNumeroDeCuenta(call);
							play.playRecordAnnouncement();							
						}else{
							logger.info("SEGUNDA OPORTUNIDAD");
							setNumeroDeOportunidadesRecord(0);
							new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
							PlayUsuarioNoEntrenado play = new PlayUsuarioNoEntrenado(call);
							play.usuarioNoEntrenado();
						}
					}
					if(existNumeroDeCuenta == false && trainedUser == false){
						//EL USUARIO NO ESTA ENTRENADO Y NO EXISTE EL NUMERO DE CUENTA (DROP CALL).
						logger.error("EL USUARIO NO ESTA ENTRENADO Y SIN NUMERO DE CUENTA");
						if(getNumeroDeOportunidadesRecord() == 0){
							logger.info("PRIMERA OPORTUNIDAD");
							setNumeroDeOportunidadesRecord(1);
							new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
							PlayRecordNumeroDeCuenta play = new PlayRecordNumeroDeCuenta(call);
							play.playRecordAnnouncement();							
						}else{
							logger.info("SEGUNDA OPORTUNIDAD");
							setNumeroDeOportunidadesRecord(0);
							new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
							new PlayNoUserProfile(call).noUser();
						}
					}
				}catch(URISyntaxException e){
					PlayError play = new PlayError(call);
					play.audioError();
					logger.error("Error SpeechToTextNumeroDeCuenta " + e.toString());
					new MyEmailSender().sendErrorByEmail("Error SpeechToTextNumeroDeCuenta " + e.toString(), call);
				}
				/*
				 * Detener musica en espera
				 */
				mediaServicehelsinky.stop(participant, requestid);
	}

	public static int getNumeroDeOportunidadesRecord() {
		return numeroDeOportunidadesRecord;
	}

	public static void setNumeroDeOportunidadesRecord(
			int numeroDeOportunidadesRecord) {
		SpeechToTextNumeroDeCuenta.numeroDeOportunidadesRecord = numeroDeOportunidadesRecord;
	}
	
}
