    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.AAADEVNaturalLanguageIVR.Bean;

import java.io.Serializable;

/**
 *
 * @author umansilla
 */
public class InputIntent implements Serializable{

	private static final long serialVersionUID = 1L;
	private String anger;
    private String fear;
    private String transcript;
    private String fechayHora;
    private String disgust;
    private String destino;
    private String joy;
    private String sadness;
    private String intent;
    private String confidence;
    private String origen;
    private String wavFile;

    public InputIntent() {
    }
    
    public InputIntent(String anger, String fear, String transcript, String fechayHora, String disgust, String destino, String joy, String sadness, String intent, String confidence, String origen, String wavFile) {
        this.anger = anger;
        this.fear = fear;
        this.transcript = transcript;
        this.fechayHora = fechayHora;
        this.disgust = disgust;
        this.destino = destino;
        this.joy = joy;
        this.sadness = sadness;
        this.intent = intent;
        this.confidence = confidence;
        this.origen = origen;
        this.wavFile = wavFile;
    }

    public InputIntent(String anger, String fear, String disgust, String joy, String sadness) {
        this.anger = anger;
        this.fear = fear;
        this.disgust = disgust;
        this.joy = joy;
        this.sadness = sadness;
    }
    
    
    

    public String getAnger() {
        return anger;
    }

    public void setAnger(String anger) {
        this.anger = anger;
    }

    public String getFear() {
        return fear;
    }

    public void setFear(String fear) {
        this.fear = fear;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getFechayHora() {
        return fechayHora;
    }

    public void setFechayHora(String fechayHora) {
        this.fechayHora = fechayHora;
    }

    public String getDisgust() {
        return disgust;
    }

    public void setDisgust(String disgust) {
        this.disgust = disgust;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getJoy() {
        return joy;
    }

    public void setJoy(String joy) {
        this.joy = joy;
    }

    public String getSadness() {
        return sadness;
    }

    public void setSadness(String sadness) {
        this.sadness = sadness;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getWavFile() {
        return wavFile;
    }

    public void setWavFile(String wavFile) {
        this.wavFile = wavFile;
    }

    @Override
    public String toString() {
        return "InputIntent{" + "anger=" + anger + ", fear=" + fear + ", transcript=" + transcript + ", fechayHora=" + fechayHora + ", disgust=" + disgust + ", destino=" + destino + ", joy=" + joy + ", sadness=" + sadness + ", intent=" + intent + ", confidence=" + confidence + ", origen=" + origen + ", wavFile=" + wavFile + '}';
    }
        
}
