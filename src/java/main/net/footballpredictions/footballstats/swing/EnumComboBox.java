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
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

/**
 * Combo-box component for choosing between values of an enumerated type.
 * @author Daniel Dyer
 */
class EnumComboBox<E extends Enum<E>> extends JComboBox
{
    @SuppressWarnings("unchecked")
    public EnumComboBox(Class<E> enumClass,
                        ResourceBundle messageResources)
    {
        try
        {
            Method valuesMethod = enumClass.getMethod("values");
            E[] values = (E[]) valuesMethod.invoke(null);
            for (E value : values)
            {
                addItem(value);
            }
            setRenderer(new EnumComboBoxRenderer(messageResources));
            setSelectedIndex(0);
        }
        catch (Exception ex)
        {
            // Will never happen if E is an enum type.
            throw new IllegalStateException(ex);
        }
    }


    /**
     * Renders enum constants for display.
     */
    private static final class EnumComboBoxRenderer<E extends Enum<E>> extends DefaultListCellRenderer
    {
        private final ResourceBundle messageResources;

        public EnumComboBoxRenderer(ResourceBundle messageResources)
        {
            this.messageResources = messageResources;
        }


        @Override
        @SuppressWarnings("unchecked")
        public Component getListCellRendererComponent(JList list,
                                                      Object object,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean hasFocus)
        {
            return super.getListCellRendererComponent(list,
                                                      describe((E) object),
                                                      index,
                                                      isSelected,
                                                      hasFocus);
        }


        private String describe(E value)
        {
            String key = "combo." + value.getClass().getSimpleName() + '.' + value.name();
            return messageResources.getString(key);
        }
    }
}
