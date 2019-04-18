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
 * Copyright (c) 2002-2019 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * @author Ezequiel Cuellar
 */

public class PreviewParametersDialog extends JDialog {

  public class OkAction extends AbstractAction {
    public OkAction() {
      putValue( Action.NAME, messages.getString( "PreviewParametersDialog.Ok" ) );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      final ReportParameterValues properties = parametersPanel.getReportParameterValues();
      if ( properties != null ) {
        final ReportParameterValues reportParameters = masterReport.getParameterValues();
        final String[] strings = properties.getColumnNames();
        for ( int i = 0; i < strings.length; i++ ) {
          final String propertyName = strings[i];
          reportParameters.put( propertyName, properties.get( propertyName ) );
        }
      }
      confirmed = true;
      dispose();
    }
  }

  public class CancelAction extends AbstractAction {
    public CancelAction() {
      putValue( Action.NAME, messages.getString( "PreviewParametersDialog.Cancel" ) );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      confirmed = false;
      dispose();
    }
  }

  private static final Log logger = LogFactory.getLog( PreviewParametersDialog.class );

  private MasterReport masterReport;
  private ParameterReportControllerPane parametersPanel;
  private boolean confirmed;
  private ResourceBundleSupport messages;
  private PreviewParametersDialog.OkAction confirmAction;

  public PreviewParametersDialog( final Frame parent, final MasterReport report ) {
    super( parent );
    initialize( report );
  }

  public PreviewParametersDialog( final Dialog parent, final MasterReport report ) {
    super( parent );
    initialize( report );
  }

  public PreviewParametersDialog( final MasterReport report ) {
    initialize( report );
  }

  private void initialize( final MasterReport report ) {
    if ( report == null ) {
      throw new NullPointerException();
    }

    masterReport = report;
    messages =
        new ResourceBundleSupport( Locale.getDefault(), SwingPreviewModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( PreviewParametersDialog.class ) );
    confirmAction = new OkAction();

    setTitle( messages.getString( "PreviewParametersDialog.Title" ) );
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( createParametersPanel(), BorderLayout.CENTER );
    contentPane.add( createButtonsPanel(), BorderLayout.SOUTH );
    setContentPane( contentPane );

    final InputMap inputMap = contentPane.getInputMap();
    final ActionMap actionMap = contentPane.getActionMap();

    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "confirm" ); // NON-NLS
    inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "cancel" ); // NON-NLS
    actionMap.put( "confirm", new OkAction() ); // NON-NLS
    actionMap.put( "cancel", new CancelAction() ); // NON-NLS

    setModal( true );
    pack();
    LibSwingUtil.centerDialogInParent( this );
  }

  private JPanel createButtonsPanel() {
    final JButton okButton = new JButton( confirmAction );
    okButton.setDefaultCapable( true );

    final JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    buttonsPanel.setBorder( BorderFactory.createMatteBorder( 1, 0, 0, 0, Color.LIGHT_GRAY ) );
    buttonsPanel.add( okButton );
    buttonsPanel.add( new JButton( new CancelAction() ) );
    return buttonsPanel;
  }

  private JScrollPane createParametersPanel() {
    parametersPanel = new ParameterReportControllerPane();
    try {
      parametersPanel.setReport( masterReport );
    } catch ( ReportProcessingException e ) {
      parametersPanel.setErrorMessage( e.getMessage() );
      logger.error( "Failed to query parameters", e ); // NON-NLS
    }
    parametersPanel.hideControls();

    final JPanel carrierPanel = new JPanel( new BorderLayout() );
    carrierPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    carrierPanel.add( parametersPanel, BorderLayout.NORTH );

    final JScrollPane result = new JScrollPane( carrierPanel );
    result.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
    return result;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  private static boolean isAllParametersHidden( final MasterReport report,
      final ReportParameterDefinition parameterDefinition ) {
    try {
      final DefaultParameterContext parameterContext = new DefaultParameterContext( report );

      try {
        final ParameterDefinitionEntry[] entries = parameterDefinition.getParameterDefinitions();
        for ( int i = 0; i < entries.length; i++ ) {
          final ParameterDefinitionEntry entry = entries[i];

          final String hiddenFormulaString = entry.getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.HIDDEN, parameterContext );

          /* if the formula is not empty , only when the value is literally "true" the formula controls the parameter */
          if ( !StringUtils.isEmpty( hiddenFormulaString ) ) {
            if ( !"true".equals( hiddenFormulaString ) ) {
              return false;
            }
          } else {
            if ( !"true".equals( entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
              ParameterAttributeNames.Core.HIDDEN, parameterContext ) ) ) {
              return false;
            }
          }
        }
      } finally {
        parameterContext.close();
      }
      return true;
    } catch ( ReportProcessingException e ) {
      return false;
    }
  }

  public static boolean process( final Window parent, final MasterReport masterReport ) {
    if ( masterReport == null ) {
      throw new NullPointerException();
    }
    final ReportParameterDefinition parameterDefinition = masterReport.getParameterDefinition();
    if ( parameterDefinition.getParameterCount() == 0 ) {
      return true;
    }

    // if all parameters are hidden, then nothing to do
    if ( isAllParametersHidden( masterReport, parameterDefinition ) ) {
      return true;
    }

    final PreviewParametersDialog parametersDialog;
    if ( parent instanceof Frame ) {
      parametersDialog = new PreviewParametersDialog( (Frame) parent, masterReport );
    } else if ( parent instanceof Dialog ) {
      parametersDialog = new PreviewParametersDialog( (Dialog) parent, masterReport );
    } else {
      parametersDialog = new PreviewParametersDialog( masterReport );
    }

    parametersDialog.setVisible( true );
    return parametersDialog.isConfirmed();
  }
}
