package service.AAADEVNaturalLanguageIVR.Util;

/**
 * Clase creada para obtener el nombre actual de archivo de audio.
 * @author umansilla
 *
 */
public enum RecordingData
{
    INSTANCE;
    private String recordingFilename = null;

    public String getRecordingFilename()
    {
        return recordingFilename;
    }

    public void setRecordingFilename(final String recordingFilename)
    {
        this.recordingFilename = recordingFilename;
    }
}