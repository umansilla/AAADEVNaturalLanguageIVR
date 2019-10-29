package service.AAADEVNaturalLanguageIVR.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.avaya.collaboration.util.logger.Logger;

/**
 * Clase creada para comprimir el archivo deseado.
 * @author umansilla
 *
 */
public class ZipUtils {
	private final Logger logger = Logger.getLogger(getClass());
    private List <String> fileList;
    private final String SOURCE_FOLDER;// SourceFolder path

    /**
     * Constructor ZipUtils
     * @param SOURCE_FOLDER
     */
    public ZipUtils(final String SOURCE_FOLDER) {
        this.SOURCE_FOLDER = SOURCE_FOLDER;
        fileList = new ArrayList < String > ();
    }

    @SuppressWarnings("finally")
	public Boolean zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            logger.info("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (String file: this.fileList) {
//            	logger.info("File Added : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();
            logger.info("Folder successfully compressed");

        } catch (IOException ex) {
            logger.error("Error ZipUtils: " + ex.toString());
            return false;
        } finally {
            try {
                zos.close();
                return true;
            } catch (IOException e) {
            	logger.error("Error ZipUtils: " + e.toString());
                return false;
            }
        }
    }

    public void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
}
