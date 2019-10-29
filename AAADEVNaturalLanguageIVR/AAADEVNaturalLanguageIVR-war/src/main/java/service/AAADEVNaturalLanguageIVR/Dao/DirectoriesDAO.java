package service.AAADEVNaturalLanguageIVR.Dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.AAADEVNaturalLanguageIVR.Bean.DirectoryAudios;
import service.AAADEVNaturalLanguageIVR.Bean.EnglishDirectory;
import service.AAADEVNaturalLanguageIVR.Bean.LanguagesDirectories;
import service.AAADEVNaturalLanguageIVR.Bean.PortuguesDirectory;
import service.AAADEVNaturalLanguageIVR.Bean.SpanishDirectory;
import service.AAADEVNaturalLanguageIVR.Interfaz.DirectoryAccess;
import service.AAADEVNaturalLanguageIVR.Util.Constants;

import com.avaya.collaboration.util.logger.Logger;
/**
 *
 * @author umansilla
 */
public class DirectoriesDAO implements DirectoryAccess {
	private final Logger logger = Logger.getLogger(getClass());
    @Override
    public List<DirectoryAudios> getAllDirectories() throws IOException {
        String dirName = Constants.PATH_TO_AUDIOS;
        logger.info(Constants.PATH_TO_AUDIOS);
        List<DirectoryAudios> directories = new ArrayList<>();
        Files.list(new File(dirName).toPath())
                .forEach(path -> {
                    try {
                        directories.add(new DirectoryAudios(path.getFileName().toString(), getLastModification(path.toString()), getLanguagesDirectory(path.toString(), path.getFileName().toString())));
                    } catch (IOException ex) {
                        logger.error("Error al crear directories: " + ex.toString());
                    }
                });
        return directories;
    }
    
    /**
     * Metodo obtiene la ultima modificación por folder 
     * 	Ejempo Audios.
     * 		Folder: Avaya_Folder
     * @param path Ruta donde se obtiene el folder de Audios.
     * @return	Retorna la ultima modificacion del folder con formato yyyy-MM-dd hh:mm aa.
     */
    private String getLastModification(String path) {
        File file = new File(path);
        long lastModified = file.lastModified();
        String pattern = "yyyy-MM-dd hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date lastModifiedDate = new Date(lastModified);
        return simpleDateFormat.format(lastModifiedDate);
    }
    /**
     * Metodo hecho para crear el objeto LanguagesDirectories haciendo un ciclo por el folder Audios.
     * @param path Ruta donde se obtiene el folder de Audios.
     * @param directory Nombre del directorio individual Ejempo Avaya_Folder.
     * @return Se retorna el objeto LanguagesDirectories.
     * @throws IOException Error en que se lanza cuando no se encuentra el folder indicado.
     */
    private LanguagesDirectories getLanguagesDirectory(String path, String directory) throws IOException {

        LanguagesDirectories languageDirectories = new LanguagesDirectories();
        Files.list(new File(path).toPath())
                .forEach(pathLanguages -> {
                    if (pathLanguages.getFileName().toString().equals("EN")) {
                        try {
                            EnglishDirectory En = getEnglishAudioFiles(pathLanguages.toString(), directory, pathLanguages.getFileName().toString());
                            languageDirectories.setEn(En);
                        } catch (IOException ex) {
                            logger.error("Error al encontrar archivos del directorio en EN " + ex.toString()) ;
                        }

                    }
                    if (pathLanguages.getFileName().toString().equals("ES")) {
                        try {
                            SpanishDirectory Es = getSpanishAudioFiles(pathLanguages.toString(), directory, pathLanguages.getFileName().toString());
                            languageDirectories.setEs(Es);
                        } catch (IOException ex) {
                        	logger.error("Error al encontrar archivos del directorio en ES " + ex.toString());
                        }

                    }
                    if (pathLanguages.getFileName().toString().equals("PT")) {
                        try {
                            PortuguesDirectory Pt = getPortugueseAudioFiles(pathLanguages.toString(), directory, pathLanguages.getFileName().toString());
                            languageDirectories.setPt(Pt);
                        } catch (IOException ex) {
                        	logger.error("Error al encontrar archivos del directorio en PT " + ex.toString());
                        }
                    }
                    if (pathLanguages.getFileName().toString().equals("IMG")) {
                        try {
                        	logger.info("IMG");
                            languageDirectories.setImage(getImage(pathLanguages.toString(), directory, pathLanguages.getFileName().toString()));
                            logger.info(getImage(pathLanguages.toString(), directory, pathLanguages.getFileName().toString()));
                        } catch (IOException ex) {
                        	logger.error("Error al encontrar archivo en IMG " + ex.toString());
                        }
                    }
                });

        return languageDirectories;
    }
    
    /**
     * Metodo creado para obtener la imagen del folder especificado.
     * @param path Ruta donde se obtiene el folder de Audios.
     * @param directoryName Nombre del directorio individual Ejempo Avaya_Folder.
     * @param languajeDirectory Nombre del directorio individual Ejempo ES.
     * @return Regresa la ruta donde se almacena la imagen de Avaya_Folder.
     * @throws IOException	Se lanza la excepcion cuando no se encuentra la imagen.
     */
    private String getImage(String path, String directoryName, String languajeDirectory) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.list(new File(path).toPath())
                .forEach(imageFile -> {
                    sb.append("Audios/" + directoryName + "/" + languajeDirectory + "/" + imageFile.getFileName().toString());
                });

        return sb.toString();
    }

    /**
     * Metodo para obtener la ruta de los audios dentro del folder EN.
     * @param path Ruta donde se obtiene el folder de Audios.
     * @param directoryName Nombre del directorio individual Ejempo Avaya_Folder.
     * @param languajeDirectory Nombre del directorio individual Ejempo EN.
     * @return Se retorna el objeto EnglishDirectory para almacenarlo en LanguagesDirectories.
     * @throws IOException Se lanza la excepcion cuando no se encuentra el folder EN.
     */
    private EnglishDirectory getEnglishAudioFiles(String path, String directoryName, String languajeDirectory) throws IOException {
        Map<String, String> map = new HashMap<>();
        Files.list(new File(path).toPath())
                .forEach(englishFiles -> {
                    map.put(englishFiles.getFileName().toString(), "Audios/" + directoryName + "/" + languajeDirectory + "/" + englishFiles.getFileName().toString());
                });
        EnglishDirectory englishFiles = new EnglishDirectory(map);
        return englishFiles;
    }
    
    /**
     * Metodo para obtener la ruta de los audios dentro del folder ES.
     * @param path Ruta donde se obtiene el folder de Audios.
     * @param directoryName Nombre del directorio individual Ejempo Avaya_Folder.
     * @param languajeDirectory Nombre del directorio individual Ejempo ES.
     * @return Se retorna el objeto EnglishDirectory para almacenarlo en LanguagesDirectories.
     * @throws IOException Se lanza la excepcion cuando no se encuentra el folder ES.
     */
    private SpanishDirectory getSpanishAudioFiles(String path, String directoryName, String languajeDirectory) throws IOException {
        Map<String, String> map = new HashMap<>();
        Files.list(new File(path).toPath())
                .forEach(spanishFiles -> {
                    map.put(spanishFiles.getFileName().toString(), "Audios/" + directoryName + "/" + languajeDirectory + "/" + spanishFiles.getFileName().toString());
                });
        SpanishDirectory spanishFiles = new SpanishDirectory(map);
        return spanishFiles;
    }

    /**
     * Metodo para obtener la ruta de los audios dentro del folder PT.
     * @param path Ruta donde se obtiene el folder de Audios.
     * @param directoryName Nombre del directorio individual Ejempo Avaya_Folder.
     * @param languajeDirectory Nombre del directorio individual Ejempo PT.
     * @return Se retorna el objeto EnglishDirectory para almacenarlo en LanguagesDirectories.
     * @throws IOException Se lanza la excepcion cuando no se encuentra el folder PT.
     */
    private PortuguesDirectory getPortugueseAudioFiles(String path, String directoryName, String languajeDirectory) throws IOException {

        Map<String, String> map = new HashMap<>();
        Files.list(new File(path).toPath())
                .forEach(portugueseFiles -> {
                    map.put(portugueseFiles.getFileName().toString(), "Audios/" + directoryName + "/" + languajeDirectory + "/" + portugueseFiles.getFileName().toString());
                });
        PortuguesDirectory portugueseFiles = new PortuguesDirectory(map);
        return portugueseFiles;
    }

}
