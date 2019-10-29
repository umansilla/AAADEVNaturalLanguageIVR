package service.AAADEVNaturalLanguageIVR.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import service.AAADEVNaturalLanguageIVR.Bean.Usuario;

import com.avaya.collaboration.call.Call;
import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para escribir los resutados del IVR.
 * @author umansilla
 *
 */
public class WriteOnDisk {
	private static final Logger logger = Logger.getLogger(WriteOnDisk.class);
	private final Call call;
	private final Usuario usuario;
	private String nombre;
	private String fecha;
	private String tiempo;

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTiempo() {
		return tiempo;
	}

	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public WriteOnDisk(final Call call, final Usuario usuario) {
		super();
		this.call = call;
		this.usuario = usuario;
	}
	
	/*
	 * Metodo creado para iniciar la fecha y hora que tendra el archivo de audio y de texto.
	 */
	public void createFiles() throws IOException{
		/*
		 * Obtener Fecha
		 */
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(System.currentTimeMillis());
		String fecha = dateFormat.format(date);
		setFecha(fecha.replaceAll("[^\\dA-Za-z]", ""));
		
		/*
		 * Obtener hora
		 */
		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ssz");
		Date hora = new Date(System.currentTimeMillis());
		String tiempo = dateFormat2.format(hora);
		setTiempo(tiempo.replaceAll("[^\\dA-Za-z]", ""));
	
		setNombre(getFecha() + "_" + getTiempo() + "_" + call.getCalledParty().getHandle() + "_" +  call.getCallingParty().getHandle());
	
		createAudio();
		createText();
	}
	
	/**
	 * Metodo creado para crear el archivo de audio.
	 * @throws IOException Se lanza error al no exisitir el archivo de audio que se va a cambiar de lugar.
	 */
	private void createAudio() throws IOException{
        File source = new File(Constants.PATH_TO_WEB_APP + "/recordingAAADEVNaturalLanguageIVR.wav");
      
        // renaming the file and moving it to a new location 
        String destination = (AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("es")) ? (Constants.ROUTE_GRABACIONES_ES)
				: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("en") ? (Constants.ROUTE_GRABACIONES_EN)
						: ((AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.IDIOMA_OPCION).equals("pt")) ? (Constants.ROUTE_GRABACIONES_PT)
								: (Constants.ROUTE_GRABACIONES_ES))));
   
        copyFileUsingStream(source , new File(destination + getNombre() + ".wav"));
	}
	
	/**
	 * Metodo creado para cambiar de lugar y reescribir el nombre del archivo de audio grabado por el usuario.
	 * @param source Lugar donde se encuentra el archivo de audio.
	 * @param dest Destino donde se pondra nuevamente el audio.
	 * @throws IOException Se lanza error al no exisitir el archivo de audio que se va a cambiar de lugar.
	 */
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
	
    /**
     * Metodo creado para crear el archivo de texto que contiene la informacion de la transcripcion y emociones.
	 * @throws IOException Se lanza error al no exisitir el archivo de audio que se va a cambiar de lugar.
     */
	private void createText() throws IOException{
		JSONObject json = new JSONObject();
        json.put("Anger", usuario.getAnger());
        json.put("Fear", usuario.getFear());
        json.put("Transcript", usuario.getTranscript());
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String fechayHOra = dateFormat.format(date);
        json.put("fechayHora", fechayHOra);
        json.put("Disgust", usuario.getDisgust());
        json.put("Destino", AttributeStore.INSTANCE.getServiceProfilesAttributeValue(call.getCalledParty(), Constants.AGENT_PHONE));
        json.put("Joy", usuario.getJoy());
        json.put("sadness", usuario.getSadness());
        json.put("COnfidence", usuario.getConfianzaTranscript());
        json.put("Origen", call.getCallingParty().getHandle());
        json.put("Intent", new JSONObject().put("Intent", usuario.getIntent()));
        FileOutputStream out = new FileOutputStream(Constants.ROUTE_INTENTS + getNombre() + ".txt");
        out.write(json.toString().getBytes());
        out.close();
        logger.fine("Archivo creado correctamente: " + Constants.ROUTE_INTENTS + getNombre() + ".txt");
	}
}
