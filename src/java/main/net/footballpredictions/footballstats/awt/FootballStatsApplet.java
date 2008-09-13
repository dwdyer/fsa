// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   � Copyright 2000-2008 Daniel W. Dyer
//
//   This program is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published by
//   the Free Software Foundation, either version 3 of the License, or
//   (at your option) any later version.
//
//   This program is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program.  If not, see <http://www.gnu.org/licenses/>.
// ============================================================================
package net.footballpredictions.footballstats.awt;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * This class provides football stats for a web page as an AWT applet.
 * @author Daniel Dyer
 * @since 26/12/2003
 */
public final class FootballStatsApplet extends Applet
{
    private static final String VERSION_STRING 	= "Version 3.0 Alpha";
    private static final String COPYRIGHT_STRING 	= "�Copyright 2000-2008, Daniel W. Dyer";
    private static final String URL_STRING = "http://fsa.footballpredictions.net";
    
    // Mapping from display name to file name for results files.
    private final Map<String, URL> dataFiles = new HashMap<String, URL>(10);
    // Mapping from display name to highlighted team.
    private final Map<String, String> highlightedTeams = new HashMap<String, String>(10);
    
    private Theme theme = null;
        
    private final Panel mainView = new Panel(new GridLayout(1, 1));
    private final Panel controlPanel = new Panel(new GridLayout(1, 1));
    private final Choice fileSelectorDropDown = new Choice();
    
    private final Map<String, StatsPanel> panels = new HashMap<String, StatsPanel>();
    private ResourceBundle res;

    
    public FootballStatsApplet()
    {
        System.out.println(getAppletInfo());
        setLayout(new BorderLayout());   
        
    }
    
    
    public void init()
    {
        System.out.println("Initialising applet...");
        removeAll();
        loadConfiguration();
        
        String curLocaleString = getParameter("locale");
        if (curLocaleString == null ){
        	System.err.println("Param locale is null");
        	curLocaleString = "en";
        }
        System.out.println("Param locale=" + curLocaleString);
        
        Locale locale = new Locale(curLocaleString);
        res = ResourceBundle.getBundle("net.footballpredictions.footballstats.messages.fsa", locale);

        // Create the various stats screens.
        LeagueTable leagueTable = new LeagueTable(false,res);
        leagueTable.setTheme(theme);
        panels.put(res.getString("fsa.panel.league_table"), leagueTable);
        
        Results results = new Results(res);
        results.setTheme(theme);
        panels.put(res.getString("fsa.panel.results"), results);
        
        LeagueTable formTable = new LeagueTable(true, res);
        formTable.setTheme(theme);
        panels.put(res.getString("fsa.panel.form"), formTable);
        
        Sequences sequences = new Sequences(res);
        sequences.setTheme(theme);
        panels.put(res.getString("fsa.panel.sequences"), sequences);
        
        Overview overview = new Overview(res);
        overview.setTheme(theme);
        panels.put(res.getString("fsa.panel.overview"), overview);
        
        HeadToHead headToHead = new HeadToHead(res);
        headToHead.setTheme(theme);
        panels.put(res.getString("fsa.panel.head_to_head"), headToHead);
        
        Attendances attendances = new Attendances(res);
        attendances.setTheme(theme);
        panels.put(res.getString("fsa.panel.attendances"), attendances);
        
        Graphs graphs = new Graphs(res);
        graphs.setTheme(theme);
        panels.put(res.getString("fsa.panel.graphs"), graphs);
        
        addToContainer(this, createMainControls(), BorderLayout.NORTH);
        addToContainer(this, createControlPanel(), BorderLayout.EAST);
        addToContainer(this, createMainView(), BorderLayout.CENTER);
        addToContainer(this, createFooter(), BorderLayout.SOUTH);
        
        // Load the first data file in the list.
        updateLeagueData();
        
        // Set initial view to league table.
        addToContainer(mainView, leagueTable.getView());
        addToContainer(controlPanel, leagueTable.getControls());
    }
    

    /**
     * Sets up the controls at the top of the applet.
     */
    private Component createMainControls()
    {
        Panel mainControls = new Panel(new GridLayout(2, 1));
        addToContainer(mainControls, createButtonPanel());
        addToContainer(mainControls, createTopBar());
        return mainControls;
    }
    

    /**
     * Creates the row of buttons for selecting the current stats screen.
     */
    private Component createButtonPanel()
    {
        Panel buttonPanel = new Panel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        
        final Button leagueTablesButton = new Button(res.getString("fsa.panel.league_table"));
        leagueTablesButton.setActionCommand(res.getString("fsa.panel.league_table"));
        final Button resultsButton = new Button(res.getString("fsa.panel.results"));
        resultsButton.setActionCommand(res.getString("fsa.panel.results"));
        final Button formTablesButton = new Button(res.getString("fsa.panel.form"));
        formTablesButton.setActionCommand(res.getString("fsa.panel.form"));
        final Button sequencesButton = new Button(res.getString("fsa.panel.sequences"));
        sequencesButton.setActionCommand(res.getString("fsa.panel.sequences"));
        final Button seasonButton = new Button(res.getString("fsa.panel.overview"));
        seasonButton.setActionCommand(res.getString("fsa.panel.overview"));
        final Button compareButton = new Button(res.getString("fsa.panel.head_to_head"));
        compareButton.setActionCommand(res.getString("fsa.panel.head_to_head"));
        final Button attendancesButton = new Button(res.getString("fsa.panel.attendances"));
        attendancesButton.setActionCommand(res.getString("fsa.panel.attendances"));
        final Button graphsButton = new Button(res.getString("fsa.panel.graphs"));
        graphsButton.setActionCommand(res.getString("fsa.panel.graphs"));

        final Button[] buttons = new Button[]{leagueTablesButton, resultsButton, formTablesButton, sequencesButton, seasonButton, compareButton, attendancesButton, graphsButton};

        // Action listener for changing the display when a button is pressed.
        ActionListener buttonListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                StatsPanel statsPanel = panels.get(ev.getActionCommand());
                mainView.removeAll();                
                controlPanel.removeAll();
                
                if (statsPanel != null)
                {
                    addToContainer(mainView, statsPanel.getView());
                    addToContainer(controlPanel, statsPanel.getControls());
                }

                for (Button button : buttons)
                {
                    button.setBackground(theme.getButtonColour());
                }
                ((Component) ev.getSource()).setBackground(theme.getSelectedButtonColour());
                
                validate();
            }
        };
        
        // Set the button colours, register the action listener and add the buttons to the panel.
        for (int i = 0; i < buttons.length; i++)
        {
            if (i == 0)
            {
                buttons[i].setBackground(theme.getSelectedButtonColour());
            }
            else
            {
                buttons[i].setBackground(theme.getButtonColour());
            }
            buttons[i].setForeground(theme.getButtonTextColour());
            buttons[i].addActionListener(buttonListener);
            
            if (i == buttons.length - 2)
            {
                constraints.gridwidth = GridBagConstraints.RELATIVE;
            }
            else if (i == buttons.length - 1)
            {
                constraints.gridwidth = GridBagConstraints.REMAINDER;
            }
            addToContainer(buttonPanel, buttons[i], constraints);            
        }
        
        return buttonPanel;
    }
    
    
    /**
     * Creates the bar below the buttons with the file selector and web link.
     */
    private Component createTopBar()
    {
        Panel topBar = new Panel(new BorderLayout());
        topBar.setBackground(theme.getTopBarColour());
        topBar.setForeground(theme.getTopBarTextColour());
        addToContainer(topBar, createFileSelector(), BorderLayout.CENTER);
        Label urlLabel = new Label(URL_STRING);
        urlLabel.setFont(theme.getBoldFont());
        urlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        urlLabel.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent ev)
            {
                try
                {
                    getAppletContext().showDocument(new URL(URL_STRING), "_blank");
                }
                catch (MalformedURLException ex)
                {
                    // Ignore.
                }
            }
        });
        addToContainer(topBar, urlLabel, BorderLayout.EAST);
        return topBar;
    }
    
    
    /**
     * Creates the file selector part of the top bar.
     */
    private Component createFileSelector()
    {
        Panel fileSelector = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addToContainer(fileSelector, new Label(res.getString("fsa.league.data")));
        fileSelectorDropDown.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                updateLeagueData();
            }
        });
        fileSelectorDropDown.setForeground(Color.black);
        addToContainer(fileSelector, fileSelectorDropDown);
        return fileSelector;
    }
    
    
    private Component createMainView()
    {
        mainView.setBackground(theme.getMainViewColour());
        mainView.setForeground(theme.getMainViewTextColour());
        return mainView;
    }
    
    
    /**
     * Configures the container for the controls that change with the selected panel.
     */
    private Component createControlPanel()
    {
        controlPanel.setBackground(theme.getControlPanelColour());
        controlPanel.setForeground(theme.getControlPanelTextColour());
        return controlPanel;
    }
    
    
    private Component createFooter()
    {
        Panel footer = new Panel(new BorderLayout());
        footer.setFont(theme.getSmallFont());
        Color backgroundColour = theme.getControlPanelColour();
        int red = backgroundColour.getRed();
        int green = backgroundColour.getGreen();
        int blue = backgroundColour.getBlue();
        footer.setBackground(backgroundColour);
        Color footerColour = new Color(red > 128 ? red - 48 : red + 48,
                                       green > 128 ? green - 48 : green + 48,
                                       blue > 128 ? blue - 48 : blue + 48);
        footer.setForeground(footerColour);
        addToContainer(footer, new Label("FSA " + VERSION_STRING), BorderLayout.WEST);
        addToContainer(footer, new Label(COPYRIGHT_STRING, Label.RIGHT), BorderLayout.CENTER);
        return footer;
    }
        

    /**
     * (Re)loads the selected data file and updates the GUI.
     */
    private void updateLeagueData()
    {
        String selection = fileSelectorDropDown.getSelectedItem();
        if (selection != null)
        {
            URL dataFileURL = dataFiles.get(selection);
            System.out.println("Loading results data...");
            LeagueSeason data = new LeagueSeason(dataFileURL, res);
            System.out.println("Done.");
        
            for (StatsPanel panel : panels.values())
            {
                invalidate();
                panel.setLeagueData(data, highlightedTeams.get(selection));
                validate();
            }
        }
        else
        {
            System.out.println("ERROR: No data files specified.");
        }
    }
    
    
    private void loadConfiguration()
    {
        System.out.println("Processing configuration options...");
        Properties properties = new Properties();
        try
        {
            URL configURL = new URL(getDocumentBase(), getParameter("config.url"));
            System.out.println("Loading configuration from " + configURL.toString());
            properties.load(configURL.openStream());
        }
        catch (IOException ex)
        {
            System.out.println("ERROR: Failed to load configuration file.");
            return;
        }
        
        String displayName;
        String fileURL;
        int count = 0;
        do
        {
            count++;
            displayName = properties.getProperty("datafile" + count + ".displayname");
            fileURL = properties.getProperty("datafile" + count + ".url");
            String highlightedTeam = properties.getProperty("datafile" + count + ".highlightteam");
            if (displayName != null && fileURL != null)
            {
                try
                {
                    Object old = dataFiles.put(displayName, new URL(getDocumentBase(), fileURL));
                    if (old == null)
                    {
                        fileSelectorDropDown.add(displayName);
                    }
                    else
                    {
                        System.out.println("WARNING: Duplicate data file entry for " + displayName);
                    }
                    if (highlightedTeam != null)
                    {
                        highlightedTeams.put(displayName, highlightedTeam);
                    }
                }
                catch (IOException ex)
                {
                    System.out.println("WARNING: Unable to open data file " + fileURL + " - " + ex.getMessage());
                }
            }
        } while (displayName != null);
        
        // Load theme (colours and fonts) from file.
        try
        {
            fileURL = properties.getProperty("themefile.url");
            theme = new Theme(new URL(getDocumentBase(), fileURL));
            System.out.println("Loaded theme from " + fileURL);
        }
        catch (IOException ex)
        {
            System.out.println("ERROR: Unable to load theme file " + fileURL + " - " + ex.getMessage());
        }
    }
    

    /**
     * Work-around for painfully slow Container.add calls in Java 1.4.
     */    
    private void addToContainer(Container container, Component component)
    {
        if (component != null)
        {
            component.setVisible(false);
            container.add(component);
            component.setVisible(true);
        }
    }

    
    /**
     * Work-around for painfully slow Container.add calls in Java 1.4.
     */
    private void addToContainer(Container container, Component component, Object constraints)
    {
        if (component != null)
        {        
            component.setVisible(false);
            container.add(component, constraints);
            component.setVisible(true);
        }
    }
        
    
    /**
     * @return Information about this applet.
     */
    public String getAppletInfo()
    {
        return "Football Statistics Applet - " + VERSION_STRING + "\n" + COPYRIGHT_STRING + "\n" + URL_STRING;
    }
}