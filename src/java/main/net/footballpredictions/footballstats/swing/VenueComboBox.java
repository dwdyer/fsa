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

import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * Combo-box component for choosing between home matches, away matches or both.
 * @author Daniel Dyer
 */
class VenueComboBox extends JComboBox
{
    public VenueComboBox(ResourceBundle messageResources)
    {
        addItem(VenueType.BOTH);
        addItem(VenueType.HOME);
        addItem(VenueType.AWAY);
        setRenderer(new VenueComboBoxRenderer(messageResources));
        setSelectedItem(VenueType.BOTH);
    }


    /**
     * Renders {@link VenueType} constants for display.
     */
    private static final class VenueComboBoxRenderer extends DefaultListCellRenderer
    {
        private final ResourceBundle messageResources;

        public VenueComboBoxRenderer(ResourceBundle messageResources)
        {
            this.messageResources = messageResources;
        }

        
        @Override
        public Component getListCellRendererComponent(JList list,
                                                      Object object,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean hasFocus)
        {
            return super.getListCellRendererComponent(list,
                                                      describe((VenueType) object),
                                                      index,
                                                      isSelected,
                                                      hasFocus);
        }


        private String describe(VenueType venue)
        {
            switch (venue)
            {
                case BOTH: return messageResources.getString("league.matches.home_away");
                case HOME: return messageResources.getString("league.matches.home");
                case AWAY: return messageResources.getString("league.matches.away");
                default: throw new IllegalStateException("Unexpected venue type: " + venue);
            }
        }
    }
}
