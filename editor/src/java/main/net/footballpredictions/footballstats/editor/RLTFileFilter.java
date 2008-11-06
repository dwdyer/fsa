package net.footballpredictions.footballstats.editor;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Filter for a JFileChooser that accepts RLT and GZipped RLT files.
 * @author Daniel Dyer
*/
class RLTFileFilter extends FileFilter
{
    static final String RLT_EXTENSION = ".rlt";
    static final String GZIPPED_RLT_EXTENSION = ".rlt.gz";

    /**
     * Accepts RLT data files, GZip-compressed RLT data files, and directories.
     * @param file The file to check.
     * @return True if the file is a directory or its extension is ".rlt" or ".rlt.gz".
     */
    public boolean accept(File file)
    {
        String name = file.getName().toLowerCase();
        return name.endsWith(RLT_EXTENSION) || name.endsWith(GZIPPED_RLT_EXTENSION) || file.isDirectory();
    }


    public String getDescription()
    {
        return "FSA Results Data File";
    }
}
