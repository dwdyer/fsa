// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2010 Daniel W. Dyer
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

import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link DataSelector} component.
 * @author Daniel Dyer
 */
public class DataSelectorTest
{
    private static final String TEST_FILE = "./data/england/premier/2005-2006.rlt";
    private static URL TEST_URL;
    static
    {
        try
        {
            TEST_URL = new File(TEST_FILE).toURI().toURL();
        }
        catch (MalformedURLException ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    private static final String CONFIG_XML
        = "<config><league name=\"England\"><division name=\"Premier League\">" +
          "<season name=\"2008/09\" href=\"" + TEST_URL + "\" /></division></league></config>";

    private Robot robot;

    private Map<String, Map<String, Map<String, URL>>> leagues = new LinkedHashMap<String, Map<String, Map<String, URL>>>();

    @BeforeClass
    public void prepareData() throws MalformedURLException
    {
        Map<String, URL> eplSeasons = new LinkedHashMap<String, URL>();
        eplSeasons.put("A", TEST_URL);
        eplSeasons.put("B", TEST_URL);
        Map<String, URL> championshipSeasons = new LinkedHashMap<String, URL>();
        championshipSeasons.put("C", TEST_URL);
        championshipSeasons.put("D", TEST_URL);

        Map<String, Map<String, URL>> englandDivisions = new LinkedHashMap<String, Map<String, URL>>();
        englandDivisions.put("Premier League", eplSeasons);
        englandDivisions.put("Championship", championshipSeasons);

        Map<String, URL> splSeasons = new LinkedHashMap<String, URL>();
        splSeasons.put("E", TEST_URL);
        splSeasons.put("F", TEST_URL);
        Map<String, URL> d1Seasons = new LinkedHashMap<String, URL>();
        d1Seasons.put("G", TEST_URL);
        d1Seasons.put("H", TEST_URL);

        Map<String, Map<String, URL>> scotlandDivisions = new LinkedHashMap<String, Map<String, URL>>();
        scotlandDivisions.put("SPL", splSeasons);
        scotlandDivisions.put("First Division", d1Seasons);

        leagues.put("England", englandDivisions);
        leagues.put("Scotland", scotlandDivisions);
    }

    
    @BeforeMethod
    public void prepareRobot()
    {
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }


    @AfterMethod
    public void cleanUp()
    {
        robot.cleanUp();
        robot = null;
    }


    /**
     * Make sure that the combo-boxes are properly populated initially.
     */
    @Test
    public void testComboBoxInitialStates()
    {
        DataSelector selector = new DataSelector(leagues);

        FrameFixture frameFixture = createFrameFixture(selector);

        String initialLeague = (String) frameFixture.comboBox("LeagueCombo").component().getSelectedItem();
        assert "England".equals(initialLeague) : "Wrong initial league: " + initialLeague;
        String initialDivision = (String) frameFixture.comboBox("DivisionCombo").component().getSelectedItem();
        assert "Premier League".equals(initialDivision) : "Wrong initial division: " + initialDivision;
        String initialSeason = (String) frameFixture.comboBox("SeasonCombo").component().getSelectedItem();
        assert "A".equals(initialSeason) : "Wrong initial season: " + initialSeason;
    }


    /**
     * Make sure that changing the selected items in the various combos results in changes to the
     * contents of the other combos. 
     */
    @Test(dependsOnMethods = "testComboBoxInitialStates")
    public void testComboChanges()
    {
        DataSelector selector = new DataSelector(leagues);

        FrameFixture frameFixture = createFrameFixture(selector);

        frameFixture.comboBox("LeagueCombo").selectItem("Scotland");
        robot.waitForIdle();
        String selectedDivision = (String) frameFixture.comboBox("DivisionCombo").component().getSelectedItem();
        assert "SPL".equals(selectedDivision) : "Wrong division: " + selectedDivision;
        String selectedSeason = (String) frameFixture.comboBox("SeasonCombo").component().getSelectedItem();
        assert "E".equals(selectedSeason) : "Wrong season: " + selectedSeason;

        frameFixture.comboBox("DivisionCombo").selectItem("First Division");
        robot.waitForIdle();
        selectedSeason = (String) frameFixture.comboBox("SeasonCombo").component().getSelectedItem();
        assert "G".equals(selectedSeason) : "Wrong season: " + selectedSeason;
    }


    @Test
    public void testConfigXML() throws IOException
    {
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(CONFIG_XML.getBytes("UTF-8"));
        DataSelector selector = new DataSelector();
        selector.loadConfig(xmlStream, null);

        FrameFixture frameFixture = createFrameFixture(selector);

        String initialLeague = (String) frameFixture.comboBox("LeagueCombo").component().getSelectedItem();
        assert "England".equals(initialLeague) : "Wrong initial league: " + initialLeague;
        String initialDivision = (String) frameFixture.comboBox("DivisionCombo").component().getSelectedItem();
        assert "Premier League".equals(initialDivision) : "Wrong initial division: " + initialDivision;
        String initialSeason = (String) frameFixture.comboBox("SeasonCombo").component().getSelectedItem();
        assert "2008/09".equals(initialSeason) : "Wrong initial season: " + initialSeason;
    }


    private FrameFixture createFrameFixture(JComponent component)
    {
        JFrame frame = new JFrame();
        frame.add(component, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(500, 60);
        frame.validate();
        frame.setVisible(true);
        return frameFixture;
    }
}
