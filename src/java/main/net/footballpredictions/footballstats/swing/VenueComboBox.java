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
    public VenueComboBox()
    {
        super(new VenueComboBoxModel());
        setRenderer(new VenueComboBoxRenderer());
        setSelectedItem(VenueType.BOTH);
    }


    /**
     * {@link javax.swing.ComboBoxModel} to display a choice of matches (home and away,
     * home only, or away only).
     */
    private static final class VenueComboBoxModel extends AbstractListModel implements ComboBoxModel
    {
        private VenueType selected = null;

        public int getSize()
        {
            return VenueType.values().length;
        }


        public VenueType getElementAt(int i)
        {
            switch (i)
            {
                case 0 : return VenueType.BOTH;
                case 1 : return VenueType.HOME;
                case 2 : return VenueType.AWAY;
                default : throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }


        public void setSelectedItem(Object object)
        {
            this.selected = (VenueType) object;
        }


        public VenueType getSelectedItem()
        {
            return selected;
        }
    }


    /**
     * Renders {@link VenueType} constants for display.
     */
    private static final class VenueComboBoxRenderer extends DefaultListCellRenderer
    {
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
                case BOTH: return "Home & Away";
                case HOME: return "Home Only";
                case AWAY: return "Away Only";
                default: throw new IllegalStateException("Unexpected venue type: " + venue);
            }
        }
    }
}
