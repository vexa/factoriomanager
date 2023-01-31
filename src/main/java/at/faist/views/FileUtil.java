package at.faist.views;

import static org.apache.commons.io.FilenameUtils.indexOfExtension;

public class FileUtil {

    public static String removeExtension(final String filename) {
        final int index = indexOfExtension(filename); //used the String.lastIndexOf() method
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }
}
