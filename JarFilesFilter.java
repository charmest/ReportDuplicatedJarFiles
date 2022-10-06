package as.ep.outils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * A filter to keep only the files which contain a ".jar" extension
 * 
 * @author Thomas CHARMES
 *
 */
public class JarFilesFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
	return name.endsWith(".jar");
    }

}
