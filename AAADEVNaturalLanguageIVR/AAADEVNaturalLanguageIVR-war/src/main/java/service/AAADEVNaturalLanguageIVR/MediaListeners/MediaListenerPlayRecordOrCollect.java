package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GetFileAccess;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayBeep;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayDigitCollectAgain;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayHelsinkiRecordNumeroDeCuenta;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayNoUserProfile;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayRecordOrCollect;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayUsuarioNoEntrenado;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayVerbioVerify;
import service.AAADEVNaturalLanguageIVR.Util.RegisterInputs;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.media.DigitCollectorOperationCause;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.call.media.RecordOperationCause;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener los resultados de la obtencion de numero de cuenta por voz o por DTMF
 * @author umansilla
 *
 */
public class MediaListenerPlayRecordOrCollect extends MediaListenerAbstract{
	private final Logger logger = Logger.getLogger(getClass());
	private Usuario usuario;
	private final Call call;
	private static Boolean recordValidate = false;
	public static int numeroDeOportunidades;
	public MediaListenerPlayRecordOrCollect(Call call) {
		super();
		this.call = call;
	}
	
	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerPlayRecordOrCollect PlayOperationCause.COMPLETE");
			PlayBeep play = new PlayBeep(call);
			play.beep();
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerPlayRecordOrCollect PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerPlayRecordOrCollect PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.info("MediaListenerPlayRecordOrCollect PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerPlayRecordOrCollect PlayOperationCause.STOPPED");
		}
	}


	@Override
	public void digitsCollected(UUID requestId, String digits,
			DigitCollectorOperationCause cause) {
		if(cause == DigitCollectorOperationCause.TERMINATION_KEY){
			logger.fine("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.TERMINATION_KEY");
			recordValidate = true;
			PlayRecordOrCollect.recordMediaService.stop(call, PlayRecordOrCollect.recordUUuid);
		}
		if(cause == DigitCollectorOperationCause.NUMBER_OF_DIGITS){
			logger.fine("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.NUMBER_OF_DIGITS");
			//DETENER la grabacion
			PlayRecordOrCollect.recordMediaService.stop(call, PlayRecordOrCollect.recordUUuid);
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
					//EXISTE EL NÚMERO DE CUENTA, CONTNUA PARA VALIDACIoN POR VERBIO.
					logger.fine("EL NUMERO DE CUENTA SI EXISTE Y EL USUARIO ESTA ENTRENADO EN VERBIO");
					new RegisterInputs().registerUserIVR(usuario, call.getCallingParty().getHandle());
					PlayVerbioVerify play = new PlayVerbioVerify(call, usuario);
					play.verify();
				}
				if(existNumeroDeCuenta == false && trainedUser == true){
					//NO EXISTE EL NUMERO DE CUENTA
					logger.info("NO EXISTE EL NUMERO DE CUENTA, VOLVER A RECOLECTAR DIGITOS");
					if(getNumeroDeOportunidades() == 0){
						logger.info("PRIMERA OPORTUNIDAD");
						setNumeroDeOportunidades(1);
						new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
						PlayDigitCollectAgain play = new PlayDigitCollectAgain(call);
						play.collectAgain();
					}else{
						logger.info("SEGUNDA OPORTUNIDAD");
						setNumeroDeOportunidades(0);
						new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
						new PlayNoUserProfile(call).noUser();;
					}
				}
				if(existNumeroDeCuenta == true && trainedUser == false){
					//EL USUARIO NO ESTA ENTRENADO (DROP CALL).
					logger.error("EL USUARIO NO ESTA ENTRENADO");
					if(getNumeroDeOportunidades() == 0){
						logger.info("PRIMERA OPORTUNIDAD");
						setNumeroDeOportunidades(1);
						new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
						PlayDigitCollectAgain play = new PlayDigitCollectAgain(call);
						play.collectAgain();
					}else{
						logger.info("SEGUNDA OPORTUNIDAD");
						setNumeroDeOportunidades(0);
						new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
						PlayUsuarioNoEntrenado play = new PlayUsuarioNoEntrenado(call);
						play.usuarioNoEntrenado();
					}
				}
				if(existNumeroDeCuenta == false && trainedUser == false){
					//EL USUARIO NO ESTA ENTRENADO Y NO EXISTE EL NUMERO DE CUENTA (DROP CALL).
					logger.error("EL USUARIO NO ESTA ENTRENADO Y SIN NUMERO DE CUENTA");
					if(getNumeroDeOportunidades() == 0){
						logger.info("PRIMERA OPORTUNIDAD");
						setNumeroDeOportunidades(1);
						new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
						PlayDigitCollectAgain play = new PlayDigitCollectAgain(call);
						play.collectAgain();
					}else{
						logger.info("SEGUNDA OPORTUNIDAD");
						setNumeroDeOportunidades(0);
						new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
						new PlayNoUserProfile(call).noUser();;
					}
				}
				
			}catch(URISyntaxException | IOException e){
				logger.error("Error MediaListenerPlayRecordOrCollect " + e.toString());
				PlayError play = new PlayError(call);
				play.audioError();
				new MyEmailSender().sendErrorByEmail("Error MediaListenerPlayRecordOrCollect " + e.toString(), call);
			}
		}
		if(cause == DigitCollectorOperationCause.TIMEOUT){
			logger.error("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.TIMEOUT");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.TIMEOUT", call);
		}
		if(cause == DigitCollectorOperationCause.STOPPED){
			logger.error("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.STOPPED");
		}
		if(cause == DigitCollectorOperationCause.FAILED){
			logger.error("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerPlayRecordOrCollect DigitCollectorOperationCause.FAILED", call);
		
		}
	}


	@Override
	public void recordCompleted(UUID requestId, RecordOperationCause cause) {
		if(cause == RecordOperationCause.TERMINATION_KEY_PRESSED){
			logger.info("MediaListenerPlayRecordOrCollect RecordOperationCause.TERMINATION_KEY_PRESSED");
		}
		if(cause == RecordOperationCause.STOPPED){
			logger.fine("MediaListenerPlayRecordOrCollect RecordOperationCause.STOPPED");
			if(recordValidate){
				PlayHelsinkiRecordNumeroDeCuenta play = new PlayHelsinkiRecordNumeroDeCuenta(call);
				play.start();
				recordValidate = false;
			}else{
				logger.info("NO SE HA GRABADO NUMERO DE CUENTA POR VOZ");
			}
		}
		if(cause == RecordOperationCause.FAILED){
			logger.error("MediaListenerPlayRecordOrCollect RecordOperationCause.FAILED");
		}
		if(cause == RecordOperationCause.PARTICIPANT_DROPPED){
			logger.error("MediaListenerPlayRecordOrCollect RecordOperationCause.PARTICIPANT_DROPPED");
		}


	}

	public int getNumeroDeOportunidades() {
		return numeroDeOportunidades;
	}

	public void setNumeroDeOportunidades(int numeroDeOportunidades) {
		MediaListenerPlayRecordOrCollect.numeroDeOportunidades = numeroDeOportunidades;
	}
		
}