// $Header: $
package net.footballpredictions.footballstats.awt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

/**
 * @author Daniel Dyer
 * @since 6/9/2005
 * @version $Revision: $
 */
public class Util
{
    private static final GridBagConstraints TABLE_WRAPPER_CONSTRAINTS = new GridBagConstraints();
    static
    {
        TABLE_WRAPPER_CONSTRAINTS.gridwidth = GridBagConstraints.REMAINDER;
        TABLE_WRAPPER_CONSTRAINTS.weightx = 1.0;
        TABLE_WRAPPER_CONSTRAINTS.weighty = 1.0;
        TABLE_WRAPPER_CONSTRAINTS.anchor = GridBagConstraints.NORTH;
        TABLE_WRAPPER_CONSTRAINTS.fill = GridBagConstraints.HORIZONTAL;
    }

    
    private Util()
    {
        // Prevent instantiation.
    }
    
    
    public static Panel wrapTable(Panel tablePanel)
    {
        Panel wrapperPanel = new Panel(new GridBagLayout());
        wrapperPanel.add(tablePanel, TABLE_WRAPPER_CONSTRAINTS);
        return wrapperPanel;
    }
    
    
    public static Panel wrapLabelPair(Label label1, Label label2)
    {
        Panel wrapperPanel = new Panel(new GridLayout(1, 2));
        wrapperPanel.add(label1);
        wrapperPanel.add(label2);
        return wrapperPanel;
    }
    
    
    public static Panel borderLayoutWrapper(Component component, String position)
    {
        Panel wrapperPanel = new Panel(new BorderLayout());
        wrapperPanel.add(component, position);
        return wrapperPanel;
    }
}