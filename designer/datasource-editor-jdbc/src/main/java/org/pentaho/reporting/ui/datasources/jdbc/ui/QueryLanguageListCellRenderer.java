/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.Component;
import javax.script.ScriptEngineFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.pentaho.reporting.ui.datasources.jdbc.Messages;

public class QueryLanguageListCellRenderer extends DefaultListCellRenderer
{
  private ScriptEngineFactory defaultValue;

  public QueryLanguageListCellRenderer()
  {
  }

  public ScriptEngineFactory getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(final ScriptEngineFactory defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public Component getListCellRendererComponent(final JList list,
                                                final Object value,
                                                final int index,
                                                final boolean isSelected,
                                                final boolean cellHasFocus)
  {
    final JLabel component = (JLabel)
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (value == null)
    {
      if (defaultValue != null)
      {
        component.setText
            (Messages.getString("QueryLanguageListCellRenderer.UseDefault", defaultValue.getLanguageName()));
      }
      else
      {
        component.setText(" ");
      }
    }
    else
    {
      final ScriptEngineFactory factory = (ScriptEngineFactory) value;
      component.setText(factory.getLanguageName());
    }
    return component;
  }
}
