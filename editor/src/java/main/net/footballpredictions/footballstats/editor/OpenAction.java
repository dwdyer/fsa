package net.footballpredictions.footballstats.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import net.footballpredictions.footballstats.data.RLTDataProvider;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.swing.DataListener;
import net.footballpredictions.footballstats.swing.SwingBackgroundTask;

/**
 * Swing action for selecting and opening an FSA data file.
 * @author Daniel Dyer
 */
class OpenAction extends AbstractAction
{
    private final Set<DataListener> listeners = Collections.synchronizedSet(new HashSet<DataListener>());
    private final JFileChooser fileChooser = new JFileChooser();
    private final Component parent;

    public OpenAction(Component parent)
    {
        super("Open...");
        this.parent = parent;
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new RLTFileFilter());
    }


    /**
     * Register a call-back to be notified when a new file is loaded.
     * @param listener The object to be notified when a file is opened.
     */
    public void addDataListener(DataListener listener)
    {
        listeners.add(listener);
    }


    /**
     * Remove a previously registered call-back.
     * @param listener The listener to de-register.
     */
    public void removeDataListener(DataListener listener)
    {
        listeners.remove(listener);
    }

    
    public void actionPerformed(ActionEvent actionEvent)
    {
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            final File dataFile = fileChooser.getSelectedFile();
            new SwingBackgroundTask<LeagueSeason>()
            {
                protected LeagueSeason performTask() throws Exception
                {
                    InputStream fileStream = new FileInputStream(dataFile);
                    try
                    {
                        if (dataFile.getName().toLowerCase().endsWith(RLTFileFilter.GZIPPED_RLT_EXTENSION))
                        {
                            fileStream = new GZIPInputStream(fileStream);
                        }
                        return new LeagueSeason(new RLTDataProvider(fileStream));
                    }
                    finally
                    {
                        fileStream.close();
                    }
                }


                @Override
                protected void postProcessing(LeagueSeason data)
                {
                    synchronized(listeners)
                    {
                        for (DataListener listener : listeners)
                        {
                            listener.setLeagueData(data);
                        }
                    }
                }
            }.execute();
        }
    }
}
