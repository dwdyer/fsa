// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2008 Daniel W. Dyer
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
package net.footballpredictions.footballstats.swing;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.footballpredictions.footballstats.data.RLTDataProvider;
import net.footballpredictions.footballstats.model.LeagueSeason;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Set of combo-boxes for selecting a data file to load.
 * @author Daniel Dyer
 */
public class DataSelector extends JPanel
{
    private Map<String, Map<String, Map<String, URL>>> leagues;

    private final JComboBox leagueCombo = new JComboBox();
    private final JComboBox divisionCombo = new JComboBox();
    private final JComboBox seasonCombo = new JComboBox();

    private final Set<DataListener> listeners = Collections.synchronizedSet(new HashSet<DataListener>());


    public DataSelector()
    {
        super(new FlowLayout(FlowLayout.LEFT));
        prepareComboListeners();

        add(leagueCombo);
        add(divisionCombo);
        add(seasonCombo);

        // Set component names to aid manipulation from FEST unit tests.
        leagueCombo.setName("LeagueCombo");
        divisionCombo.setName("DivisionCombo");
        seasonCombo.setName("SeasonCombo");        
    }


    /**
     * @param leagues Maps league names to a map of divisions in that league,
     * which maps division names to a set of seasons for which data is available
     * (each with an associated URL for the data file).
     */
    public DataSelector(Map<String, Map<String, Map<String, URL>>> leagues)
    {
        this();
        this.leagues = leagues;

        for (String league : leagues.keySet())
        {
            leagueCombo.addItem(league);
        }
    }


    /**
     * @param configStream An {@link InputStream} from which the XML config is read.
     */
    public void loadConfig(InputStream configStream,
                           URL baseURL) throws IOException
    {
        try
        {
            Map<String, Map<String, Map<String, URL>>> leagues = new LinkedHashMap<String, Map<String, Map<String, URL>>>();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            Document document = domFactory.newDocumentBuilder().parse(configStream);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression leaguesQuery = xpath.compile("//league");
            XPathExpression divisionQuery = xpath.compile("./division");
            XPathExpression seasonQuery = xpath.compile("./season");

            NodeList leagueNodes = (NodeList) leaguesQuery.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < leagueNodes.getLength(); i++)
            {
                Node leagueNode = leagueNodes.item(i);
                NodeList divisionNodes = (NodeList) divisionQuery.evaluate(leagueNode, XPathConstants.NODESET);
                Map<String, Map<String, URL>> divisions = new LinkedHashMap<String, Map<String, URL>>();
                leagues.put(getAttribute(leagueNode, "name"), divisions);
                for (int j = 0; j < divisionNodes.getLength(); j++)
                {
                    Node divisionNode = divisionNodes.item(j);
                    NodeList seasonNodes = (NodeList) seasonQuery.evaluate(divisionNode, XPathConstants.NODESET);
                    Map<String, URL> seasons = new LinkedHashMap<String, URL>();
                    divisions.put(getAttribute(divisionNode, "name"), seasons);
                    for (int k = 0; k < seasonNodes.getLength(); k++)
                    {
                        Node seasonNode = seasonNodes.item(k);
                        seasons.put(getAttribute(seasonNode, "name"), new URL(baseURL, getAttribute(seasonNode, "href")));
                    }
                }
            }
            this.leagues = leagues;

            leagueCombo.removeAllItems();
            for (String league : leagues.keySet())
            {
                leagueCombo.addItem(league);
            }
        }
        catch (ParserConfigurationException ex)
        {
            throw new ConfigurationException("XML parsing error.", ex);
        }
        catch (XPathExpressionException ex)
        {
            throw new ConfigurationException("XML parsing error.", ex);
        }
        catch (SAXException ex)
        {
            throw new ConfigurationException("XML parsing error.", ex);
        }
        finally
        {
            configStream.close();
        }
    }


    private static String getAttribute(Node node, String name)
    {
        return node.getAttributes().getNamedItem(name).getTextContent();
    }


    private void prepareComboListeners()
    {
        // When the selected league changes, update the divisions combo.
        leagueCombo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    String selectedLeague = (String) itemEvent.getItem();
                    divisionCombo.removeAllItems();
                    for (String division : leagues.get(selectedLeague).keySet())
                    {
                        divisionCombo.addItem(division);
                    }
                }
            }
        });

        // When the selected division changes, update the seasons combo.
        divisionCombo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    String selectedLeague = (String) leagueCombo.getSelectedItem();
                    String selectedDivision = (String) itemEvent.getItem();
                    seasonCombo.removeAllItems();
                    for (String season : leagues.get(selectedLeague).get(selectedDivision).keySet())
                    {
                        seasonCombo.addItem(season);
                    }
                }
            }
        });

        // When the selected season changes, load the data file and notify registered listeners.
        seasonCombo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    String selectedLeague = (String) leagueCombo.getSelectedItem();
                    String selectedDivision = (String) divisionCombo.getSelectedItem();
                    String selectedSeason = (String) itemEvent.getItem();

                    final URL dataURL = leagues.get(selectedLeague).get(selectedDivision).get(selectedSeason);
                    final Container topLevelContainer = getTopLevelAncestor();
                    if (topLevelContainer != null)
                    {
                        topLevelContainer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    }
                    new SwingBackgroundTask<LeagueSeason>()
                    {
                        protected LeagueSeason performTask() throws Exception
                        {
                            InputStream inputStream = dataURL.openStream();
                            // We can also handle GZipped RLT files.
                            if (dataURL.getFile().endsWith(".gz"))
                            {
                                inputStream = new GZIPInputStream(inputStream);
                            }
                            return new LeagueSeason(new RLTDataProvider(inputStream));
                        }

                        @Override
                        protected void postProcessing(LeagueSeason data)
                        {
                            synchronized (listeners)
                            {
                                for (DataListener listener : listeners)
                                {
                                    listener.setLeagueData(data);
                                }
                                if (topLevelContainer != null)
                                {
                                    getTopLevelAncestor().setCursor(null);
                                }
                            }
                        }
                    }.execute();
                }
            }
        });
    }


    public void addDataListener(DataListener listener)
    {
        listeners.add(listener);
    }


    public void removeDataListener(DataListener listener)
    {
        listeners.remove(listener);
    }
}
