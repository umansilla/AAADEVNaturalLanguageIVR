package service.AAADEVNaturalLanguageIVR.Interfaz;

import java.io.IOException;
import java.util.List;

import service.AAADEVNaturalLanguageIVR.Bean.DirectoryAudios;
/**
 *
 * @author umansilla
 */
public interface DirectoryAccess {
	/**
	 * Metodo creado para recuperar todos los archivos del folder Audios.
	 * @return Bean List<DirectoryAudios>.
	 * @throws IOException Error lanzado en caso que exista un error en el archivo Audios.
	 */
    public List<DirectoryAudios> getAllDirectories() throws IOException;
}
