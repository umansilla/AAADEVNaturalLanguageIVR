package service.AAADEVNaturalLanguageIVR.MediaListeners;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.MyEmailSender;
import service.AAADEVNaturalLanguageIVR.Bean.Usuario;
import service.AAADEVNaturalLanguageIVR.Http.GetFileAccess;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayError;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayNoUser;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayRecordOrCollect;
import service.AAADEVNaturalLanguageIVR.PlayAnnouncement.PlayWelcomeVerify;
import service.AAADEVNaturalLanguageIVR.Util.RegisterInputs;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.call.Participant;
import com.avaya.collaboration.call.media.MediaListenerAbstract;
import com.avaya.collaboration.call.media.PlayOperationCause;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para obtener los resultados de la reproduccion del audio ringout.wav
 * @author umansilla
 *
 */
public class MediaListenerRingOut extends MediaListenerAbstract{
	private final Call call;
	private final Logger logger = Logger.getLogger(getClass());
	public MediaListenerRingOut(final Call call) {
		this.call = call;
	}

	@Override
	public void playCompleted(UUID requestId, PlayOperationCause cause) {
		if(cause == PlayOperationCause.COMPLETE){
			logger.fine("MediaListenerRingOut PlayOperationCause.COMPLETE");
			if(call.isCalledPhase()){
				try{
					GetFileAccess get = new GetFileAccess();
					String jsonData = get.fileHttp();
					JSONArray jobj = new JSONArray(jsonData);
					for (int i = 0; i < jobj.length(); i++) {
						String phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "null";
						Participant callingParticipant = call.getCallingParty();
						if (phone.equals(callingParticipant.getHandle())) {
							//Se ha encontrado coincidencia en el teléfono
							String train = jobj.getJSONObject(i).has("train")?jobj.getJSONObject(i).getString("train"):"no";
							if (train.equals("yes")) {
								//EL USUARIO ESTA ENTRENADO SE RECONOCE EL TELEFONO. //SOLO SE VERIFICARA POR VOZ
								//PUEDDE OBTENER SALDO Y CONSULTAR MOVIMIENTOS
								try {	
									String userName = jobj.getJSONObject(i).has("username")?jobj.getJSONObject(i).getString("username"):"";
					                String name = jobj.getJSONObject(i).has("name") ? jobj.getJSONObject(i).getString("name") : "";
					                String verbiouser = jobj.getJSONObject(i).has("verbiouser") ? jobj.getJSONObject(i).getString("verbiouser") : "";
					                String fecha = jobj.getJSONObject(i).has("fecha") ? jobj.getJSONObject(i).getString("fecha") : "";
					                String hora = jobj.getJSONObject(i).has("hora") ? jobj.getJSONObject(i).getString("hora") : "";
					                phone = jobj.getJSONObject(i).has("phone") ? jobj.getJSONObject(i).getString("phone") : "";
					                train = jobj.getJSONObject(i).has("train") ? jobj.getJSONObject(i).getString("train") : "";
					                String country = jobj.getJSONObject(i).has("country") ? jobj.getJSONObject(i).getString("country") : "";
					                Boolean cajaSocialExists = jobj.getJSONObject(i).has("Caja_Social")?true:false;
					                String cuenta = "";
					                String saldo = "";
					                ArrayList<String> historicoList = null;
					                if(cajaSocialExists){
					                    JSONObject cajaSocial = jobj.getJSONObject(i).getJSONObject("Caja_Social");
					                    cuenta = cajaSocial.getString("Cuenta_Caja_Social");
					                    saldo = cajaSocial.getString("Saldo_Caja_Social");
					                    JSONArray cajaSocialArray = cajaSocial.getJSONArray("Historico_Caja_Social");
					                    historicoList = new ArrayList<String>();
					                    for (int j = 0; j <= cajaSocialArray.length() - 1; j++) {
					                        historicoList.add(cajaSocialArray.getString(j));
					                    }
					                }
					               
							        Usuario usuario = new Usuario(jobj.getJSONObject(i).getInt("id"), name, verbiouser, userName, fecha, hora, phone, train, country, cuenta, historicoList, saldo, true);										
							        new RegisterInputs().registerUserIVR(usuario, call.getCallingParty().getHandle());
							        PlayWelcomeVerify play = new PlayWelcomeVerify(call, usuario);	
									play.playWelcomeVerify();
									break;
								} catch (URISyntaxException e) {
									logger.error("URISyntaxExceptions "+ e.toString());
									new MyEmailSender().sendErrorByEmail("CallListener "+e.toString(), call);
								}
							}else {
								//EL USUARIO NO ESTA ENTRENADO 
								//AAADEVRECORD ORIGINAL
								//SE REPRODUCE MENSAJE ORIGINAL DONDE SOLO PUEDE DAR REPORTE Y SE TRANSFIERE AL AREA INDICADA
								logger.error("EL USUARIO NO ESTA ENTRENADO");
								new RegisterInputs().registerIVR(call.getCallingParty().getHandle());
								PlayNoUser play = new PlayNoUser(call, new Usuario(false));
								play.userNotRegistered();
								break;
							}
						}
						if (i == (jobj.length() - 1)) {
							//NO SE ENCONTRo EL TELÉFONO, SE VERIFICA SI EL USUARIO TIENE
							//NÚMERO DE CUENTA. PARA SER IDENTIFICADO POSTERIORMENTE POR VOZ
							logger.error("EL USUARIO SERA IDENTIFICADO POR NUMERO DE CUENTA");
							PlayRecordOrCollect play = new PlayRecordOrCollect(call);
							play.collect();
							play.record();
							break;

						}
					}
					
				}catch(Exception e){
					logger.error("CallListener "+e.toString());
					new MyEmailSender().sendErrorByEmail("CallListener "+e.toString(), call);
				}
			}else {
				logger.info("Snap-in sequenced in calling phase");
				call.allow();
			}
		}
		if(cause == PlayOperationCause.FAILED){
			logger.error("MediaListenerRingOut PlayOperationCause.FAILED");
			new PlayError(call).audioError();
			new MyEmailSender().sendErrorByEmail("MediaListenerRingOut PlayOperationCause.FAILED", call);
		}
		if(cause == PlayOperationCause.INTERRUPTED){
			logger.error("MediaListenerRingOut PlayOperationCause.INTERRUPTED");
		}
		if(cause == PlayOperationCause.STOPPED){
			logger.info("MediaListenerRingOut PlayOperationCause.STOPPED");
		}
	}
}
