package service.AAADEVNaturalLanguageIVR.Bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 *
 * @author umansilla
 */
public class Usuario {

	private int id;
	private String name;
	private String verbiouser;
	private String username;
	private String password;
	private String fecha;
	private String hora;
	private String phone;
	private String train;
	private String country;
	private String language;
	// ///////////////////////////////////////////
	private String cuenta;
	private String saldo;
	private ArrayList<String> historicoList;
	// //////////////////////////////////////////
	private Boolean userRegistered;
	private String transcript;
	private String confianzaTranscript;
	private String intent;
	private JSONObject jsonToWrite;
	//////////////////////////////////////
	private String anger;
	private String fear;
	private String disgust;
	private String joy;
	private String sadness;
	private String admin;
	///////////////////////////////////////
	private String entity;
	private String callDivertTo;

	public Usuario() {
	}

	public Usuario(Boolean userRegistered) {
		this.userRegistered = userRegistered;
	}

	public Usuario(int id, String username) {
		this.id = id;
		this.username = username;
	}
	
	

	public Usuario(int id, String name, String verbiouser, String username,
			String fecha, String hora, String phone, String train,
			String country, String language, String cuenta, String saldo,
			ArrayList<String> historicoList) {
		super();
		this.id = id;
		this.name = name;
		this.verbiouser = verbiouser;
		this.username = username;
		this.fecha = fecha;
		this.hora = hora;
		this.phone = phone;
		this.train = train;
		this.country = country;
		this.language = language;
		this.cuenta = cuenta;
		this.saldo = saldo;
		this.historicoList = historicoList;
	}
	
	public Usuario(int id, String name, String verbiouser, String username,
			String fecha, String hora, String phone, String train,
			String country, String language, String cuenta, String saldo,
			ArrayList<String> historicoList, String admin) {
		super();
		this.id = id;
		this.name = name;
		this.verbiouser = verbiouser;
		this.username = username;
		this.fecha = fecha;
		this.hora = hora;
		this.phone = phone;
		this.train = train;
		this.country = country;
		this.language = language;
		this.cuenta = cuenta;
		this.saldo = saldo;
		this.historicoList = historicoList;
		this.admin = admin;
	}

	public Usuario(int id, String name, String username, String fecha,
			String hora, String phone, String country, String language) {
		super();
		this.id = id;
		this.name = name;
		this.username = username;
		this.fecha = fecha;
		this.hora = hora;
		this.phone = phone;
		this.country = country;
		this.language = language;
	}

	public Usuario(int id, String name, String verbiouser, String username,
			String fecha, String hora, String phone, String train,
			String country) {
		this.id = id;
		this.name = name;
		this.verbiouser = verbiouser;
		this.username = username;
		this.fecha = fecha;
		this.hora = hora;
		this.phone = phone;
		this.train = train;
		this.country = country;
	}

	// CREADO EL 10 DE JULIO 2019
	public Usuario(int id, String name, String verbiouser, String username,
			String fecha, String hora, String phone, String train,
			String country, String cuenta, ArrayList<String> historicoList,
			String saldo, Boolean userRegistered) {
		this.id = id;
		this.name = name;
		this.verbiouser = verbiouser;
		this.username = username;
		this.fecha = fecha;
		this.hora = hora;
		this.phone = phone;
		this.train = train;
		this.country = country;
		this.cuenta = cuenta;
		this.historicoList = historicoList;
		this.saldo = saldo;
		this.userRegistered = userRegistered;
	}

	// /////////////////////////////////////////

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVerbiouser() {
		return verbiouser;
	}

	public void setVerbiouser(String verbiouser) {
		this.verbiouser = verbiouser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTrain() {
		return train;
	}

	public void setTrain(String train) {
		this.train = train;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	// ///////////////////////////////////////////////
	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public List<String> getHistoricoList() {
		return historicoList;
	}

	public void setHistoricoList(ArrayList<String> historicoList) {
		this.historicoList = historicoList;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	// ///////////////////////////////////////////////

	public Boolean getUserRegistered() {
		return userRegistered;
	}

	public void setUserRegistered(Boolean userRegistered) {
		this.userRegistered = userRegistered;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public JSONObject getJsonToWrite() {
		return jsonToWrite;
	}

	public void setJsonToWrite(JSONObject jsonToWrite) {
		setSadness(jsonToWrite.getJSONObject("emotion").getJSONObject("document").getJSONObject("emotion").getString("sadness"));
		setJoy(jsonToWrite.getJSONObject("emotion").getJSONObject("document").getJSONObject("emotion").getString("joy"));
		setFear(jsonToWrite.getJSONObject("emotion").getJSONObject("document").getJSONObject("emotion").getString("fear"));
		setDisgust(jsonToWrite.getJSONObject("emotion").getJSONObject("document").getJSONObject("emotion").getString("disgust"));
		setAnger(jsonToWrite.getJSONObject("emotion").getJSONObject("document").getJSONObject("emotion").getString("anger"));
		this.jsonToWrite = jsonToWrite;
		
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getConfianzaTranscript() {
		return confianzaTranscript;
	}

	public void setConfianzaTranscript(String confianzaTranscript) {
		this.confianzaTranscript = confianzaTranscript;
	}

	public String getAnger() {
		return anger;
	}

	private void setAnger(String anger) {
		this.anger = anger;
	}

	public String getFear() {
		return fear;
	}

	private void setFear(String fear) {
		this.fear = fear;
	}

	public String getDisgust() {
		return disgust;
	}

	private void setDisgust(String disgust) {
		this.disgust = disgust;
	}

	public String getJoy() {
		return joy;
	}

	private void setJoy(String joy) {
		this.joy = joy;
	}

	public String getSadness() {
		return sadness;
	}

	private void setSadness(String sadness) {
		this.sadness = sadness;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}	
	
	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCallDivertTo() {
		return callDivertTo;
	}

	public void setCallDivertTo(String callDivertTo) {
		this.callDivertTo = callDivertTo;
	}
}