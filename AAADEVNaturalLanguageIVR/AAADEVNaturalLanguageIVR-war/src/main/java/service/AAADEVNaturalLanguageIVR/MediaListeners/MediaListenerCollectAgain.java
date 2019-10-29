package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GetFileAccess;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayDigitCollectAgain;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayNoUserProfile;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayUsuarioNoEntrenado;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayVerbioVerify;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para Obtener los digitos marcados por DTMF del calling party.
 * @author umansilla
 *
 */
public class MediaListenerCollectAgain extends MediaListenerAbstract{
	private final Call call;
	private Usuario usuario;
	private final Logger logger = Logger.getLogger(getClass());
	public MediaListenerCollectAgain(final Call call) {
		this.call = call;
	}
	@Override
	public void digitsCollected(final UUID requestId, final String digits,
			final DigitCollectorOperationCause cause) {
		
		if(cause == DigitCollectorOperationCause.NUMBER_OF_DIGITS){
			//RECUPERAR EL NUMERO DE CUENTA Y DESPUES VALIDACION POR VERBIO.
			Boolean existNumeroDeCuenta = false;
			Boolean trainedUser = false;
			GetFileAccess get = new GetFileAccess();
			String jsonData = get.fileHttp();
			JSONArray jobj = new JSONArray(jsonData);
			for (int i = 0; i < jobj.length(); i++){
				if(jobj.getJSONObject(i).has("Caja_Social")){
					JSONObject cajaSocial = jobj.getJSONObject(i).getJSONObject("Caja_Social");
					String cuentaCajaSocial = cajaSocial.getString("Cuenta_Caja_Social");
					if(digits.equals(cuentaCajaSocial)){
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
					//EXISTE EL NÚMERO DE CUENTA, CONTNUA PARA VALIDACIoN POR VERBIO. (VALDACIoN CON VERBIO).
					if(MediaListenerPlayRecordOrCollect.numeroDeOportunidades == 1){
						MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
					}
					logger.fine("EL NUMERO DE CUENTA SI EXISTE Y EL USUARIO ESTA ENTRENADO EN VERBIO");
					MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
					PlayVerbioVerify play = new PlayVerbioVerify(call, usuario);
					play.verify();
					
				}
				if(existNumeroDeCuenta == false && trainedUser == true){
					//NO EXISTE EL NUMERO DE CUENTA (DIGITAR NUEVAMENTE).
					logger.info("NO EXISTE EL NUMERO DE CUENTA");
					if(MediaListenerPlayRecordOrCollect.numeroDeOportunidades == 1){
						MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
						new PlayNoUserProfile(call).noUser();
					}else{
						MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
						PlayDigitCollectAgain play = new PlayDigitCollectAgain(call);
						play.collectAgain();
					}
					
				}
				if(existNumeroDeCuenta == true && trainedUser == false){
					//EL USUARIO NO ESTA ENTRENADO (DROP CALL).
					logger.info("EL USUARIO NO ESTA ENTRENADO");
					MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
					PlayUsuarioNoEntrenado play = new PlayUsuarioNoEntrenado(call);
					play.usuarioNoEntrenado();
				}
				if(existNumeroDeCuenta == false && trainedUser == false){
					//EL USUARIO NO ESTA ENTRENADO Y NO EXISTE EL NUMERO DE CUENTA.
					logger.info("EL USUARIO NO ESTA ENTRENADO Y NO EXISTE EL NUMERO DE CUENTA");
					if(MediaListenerPlayRecordOrCollect.numeroDeOportunidades == 1){
						MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
						new PlayNoUserProfile(call).noUser();
					}else{
						MediaListenerPlayRecordOrCollect.numeroDeOportunidades = 0;
						PlayDigitCollectAgain play = new PlayDigitCollectAgain(call);
						play.collectAgain();
					}
				}
			}catch(URISyntaxException e){
				PlayError play = new PlayError(call);
				play.audioError();
				logger.error("Error MediaListenerCollectAgain " + e.toString());
				new MyEmailSender().sendErrorByEmail("Error MediaListenerCollectAgain " + e.toString(), call);
			}
		}
		if(cause == DigitCollectorOperationCause.FAILED){
			logger.error("MediaListenerCollectAgain DigitCollectorOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerCollectAgain DigitCollectorOperationCause.FAILED", call);
		}
		if(cause == DigitCollectorOperationCause.STOPPED){
			logger.error("MediaListenerCollectAgain DigitCollectorOperationCause.STOPPED");
		}
		if(cause == DigitCollectorOperationCause.TERMINATION_KEY){
			logger.error("MediaListenerCollectAgain DigitCollectorOperationCause.TERMINATION_KEY");
		}
		if(cause == DigitCollectorOperationCause.TIMEOUT){
			logger.error("MediaListenerCollectAgain DigitCollectorOperationCause.TIMEOUT");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerCollectAgain DigitCollectorOperationCause.TIMEOUT", call);
		}
	}
}
