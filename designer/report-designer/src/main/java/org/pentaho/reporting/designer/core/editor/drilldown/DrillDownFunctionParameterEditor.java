/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FunctionParameterContext;
import org.pentaho.openformula.ui.ParameterUpdateEvent;
import org.pentaho.openformula.ui.ParameterUpdateListener;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.ReportDesignerFunctionParameterEditor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DrillDownFunctionParameterEditor implements ReportDesignerFunctionParameterEditor {
  private class DrillDownUpdateListener implements PropertyChangeListener {
    private DrillDownUpdateListener() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( inSetupUpdate ) {
        return;
      }
      inParameterUpdate = true;
      try {
        final ParameterUpdateListener[] parameterUpdateListeners =
          eventListenerList.getListeners( ParameterUpdateListener.class );
        String formula = editor.getDrillDownFormula();
        if ( formula == null ) {
          formula = "DRILLDOWN(\"Text\"; \"Text\"; \"Any\")";  // NON-NLS
        }
        for ( int i = 0; i < parameterUpdateListeners.length; i++ ) {
          final ParameterUpdateListener listener = parameterUpdateListeners[ i ];
          listener
            .parameterUpdated( new ParameterUpdateEvent( DrillDownFunctionParameterEditor.this, -1, formula, true ) );
        }
      } finally {
        inParameterUpdate = false;
      }
    }
  }

  private class DrillDownItemListener implements ChangeListener {
    private DrillDownItemListener() {
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      final DrillDownUiProfile uiProfile = drillDownSelector.getSelectedProfile();
      editor.setDrillDownUiProfile( uiProfile );
    }
  }


  private class DrillDownProfileChangeHandler implements PropertyChangeListener {
    private DrillDownProfileChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( DrillDownEditor.DRILL_DOWN_UI_PROFILE_PROPERTY.equals( evt.getPropertyName() ) == false ) {
        return;
      }

      drillDownSelector.setSelectedProfile( editor.getDrillDownUiProfile() );

    }
  }

  private DrillDownEditor editor;
  private EventListenerList eventListenerList;
  private ReportDesignerContext designerContext;
  private ComboBoxSelector drillDownSelector;
  private JPanel panel;
  private boolean inParameterUpdate;
  private boolean inSetupUpdate;
  private FieldDefinition[] fieldDefinitions;

  public DrillDownFunctionParameterEditor() {
    drillDownSelector = new ComboBoxSelector( false );
    drillDownSelector.addChangeListener( new DrillDownItemListener() );

    editor = new DrillDownEditor();
    editor.setEditFormulaFragment( true );
    editor.setLimitedEditor( true );
    editor.addPropertyChangeListener( "drillDownFormula", new DrillDownUpdateListener() );
    editor.addPropertyChangeListener( DrillDownEditor.DRILL_DOWN_UI_PROFILE_PROPERTY,
            new DrillDownProfileChangeHandler() );

    eventListenerList = new EventListenerList();

    final JPanel selectorPanel = new JPanel();
    selectorPanel.setLayout( new BorderLayout() );
    selectorPanel.add( new JLabel( "Location:" ), BorderLayout.NORTH );
    selectorPanel.add( drillDownSelector.getComponent(), BorderLayout.WEST );

    panel = new JPanel( new BorderLayout() );
    panel.add( selectorPanel, BorderLayout.NORTH );
    panel.add( editor, BorderLayout.CENTER );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return designerContext;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.designerContext = reportDesignerContext;
  }

  public void addParameterUpdateListener( final ParameterUpdateListener parameterUpdateListener ) {
    eventListenerList.add( ParameterUpdateListener.class, parameterUpdateListener );
  }

  public void removeParameterUpdateListener( final ParameterUpdateListener parameterUpdateListener ) {
    eventListenerList.remove( ParameterUpdateListener.class, parameterUpdateListener );
  }

  public Component getEditorComponent() {
    return panel;
  }

  public void setFields( final FieldDefinition[] fieldDefinitions ) {
    this.fieldDefinitions = fieldDefinitions.clone();
  }

  public void clearSelectedFunction() {
    // clean-up
  }

  private String[] filterExtraFields() {
    if ( fieldDefinitions == null ) {
      return new String[ 0 ];
    }
    if ( designerContext == null ) {
      throw new IllegalStateException();
    }
    final ReportDocumentContext activeContext = designerContext.getActiveContext();
    if ( activeContext == null ) {
      return new String[ 0 ];
    }
    final HashSet<String> columnNames =
            new HashSet<String>( Arrays.asList( activeContext.getReportDataSchemaModel().getColumnNames() ) );
    final ArrayList<String> retval = new ArrayList<String>();
    for ( int i = 0; i < fieldDefinitions.length; i++ ) {
      final FieldDefinition fieldDefinition = fieldDefinitions[ i ];
      if ( columnNames.contains( fieldDefinition.getName() ) == false ) {
        retval.add( fieldDefinition.getName() );
      }
    }
    return retval.toArray( new String[ retval.size() ] );
  }

  public void setSelectedFunction( final FunctionParameterContext context ) {
    if ( inParameterUpdate ) {
      return;
    }
    inSetupUpdate = true;
    try {
      // Editor expects the formula to have a valid prefix, so we provide one ..
      if ( editor.initialize( designerContext, context.getFunctionInformation().getFunctionText(),
        null, null, filterExtraFields() ) ) {
        if ( editor.getDrillDownUiProfile() == null ) {
          editor.setDefaultProfile();
        }
      }

    } finally {
      inSetupUpdate = false;
    }
  }
}
