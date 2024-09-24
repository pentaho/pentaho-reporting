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

package org.pentaho.reporting.engine.classic.wizard.ui.xul;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessor;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessorUtil;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.LinearWizardController;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardContentPanel;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardController;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.steps.DataSourceAndQueryStep;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.steps.FormatStep;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.steps.LayoutStep;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.steps.LookAndFeelStep;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.DefaultBindingFactory;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.swing.SwingXulLoader;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EmbeddedWizard {
  private static final String MAIN_WIZARD_PANEL =
    "org/pentaho/reporting/engine/classic/wizard/ui/xul/res/main_wizard_panel.xul"; //$NON-NLS-1$

  private Window owner;
  private DesignTimeContext designTimeContext;
  private XulDialog dialog;

  private LinearWizardController wizardController;
  private DataSourceAndQueryStep dataSourceAndQueryStep;

  public EmbeddedWizard() throws HeadlessException {
    this( null );
  }

  public EmbeddedWizard( final Window owner ) throws HeadlessException {
    this( owner, null );
    init();
  }

  public EmbeddedWizard( final Window owner, final DesignTimeContext designTimeContext ) throws HeadlessException {
    this.owner = owner;
    this.designTimeContext = designTimeContext;
    init();
  }


  private void init() {
    dataSourceAndQueryStep = new DataSourceAndQueryStep();

    wizardController = new LinearWizardController( new WizardEditorModel(), new DefaultBindingFactory() );

    // add the steps ..
    wizardController.addStep( new LookAndFeelStep() );
    wizardController.addStep( dataSourceAndQueryStep );
    wizardController.addStep( new LayoutStep() );
    wizardController.addStep( new FormatStep() );

    wizardController.addPropertyChangeListener( WizardController.CANCELLED_PROPERTY_NAME, new CancelHandler() );
    wizardController.addPropertyChangeListener( WizardController.FINISHED_PROPERTY_NAME, new FinishedHandler() );
  }

  public AbstractReportDefinition run( final AbstractReportDefinition original ) throws ReportProcessingException {
    // Set the report if we have one otherwise create a new one
    if ( original != null ) {
      wizardController.getEditorModel().setReportDefinition( original, true );
    } else {
      final MasterReport report = new MasterReport();
      report.setDataFactory( new CompoundDataFactory() );
      report.setQuery( null );
      report.setAutoSort( Boolean.TRUE );
      wizardController.getEditorModel().setReportDefinition( report, false );
    }

    // Create the gui
    try {
      final SwingXulLoader loader = new SwingXulLoader();
      loader.setOuterContext( owner );

      final XulDomContainer mainWizardContainer = new SwingXulLoader().loadXul( MAIN_WIZARD_PANEL );
      new WizardContentPanel( wizardController ).addContent( mainWizardContainer );
      mainWizardContainer.setOuterContext( this.owner );
      wizardController.registerMainXULContainer( mainWizardContainer );
      wizardController.onLoad();

      final Document documentRoot = mainWizardContainer.getDocumentRoot();
      final XulComponent root = documentRoot.getRootElement();

      if ( !( root instanceof XulDialog ) ) {
        throw new XulException(
          Messages.getInstance().getString( "EMBEDDED_WIZARD.Root_Error" ) + " " + root ); //$NON-NLS-1$ //$NON-NLS-2$
      }

      dialog = (XulDialog) root;
      // This is a hack to get the JDialog (this wizard) to become the parent window of windows/dialogs
      // that the wizard creates.
      final DesignTimeContext context = new DefaultWizardDesignTimeContext
        ( wizardController.getEditorModel(), (Window) dialog.getRootObject(), designTimeContext );
      dataSourceAndQueryStep.setDesignTimeContext( context );
      wizardController.setDesignTimeContext( context );

      // if we're doing an edit drop into the layout step
      if ( wizardController.getEditorModel().isEditing() ) {
        if ( wizardController.getStep( 0 ).isValid() ) {
          wizardController.setActiveStep( 1 ); // initializes the data
          if ( wizardController.getStep( 1 ).isValid() ) {
            wizardController.setActiveStep( 2 );
          }
        }
      }

      dialog.show();

    } catch ( Exception e ) {
      DebugLog.log( "Failed to initialize the wizard", e );
      return null;
    }

    // -----------------------------------
    if ( !wizardController.isFinished() ) {
      return null;
    }
    final AbstractReportDefinition reportDefinition = wizardController.getEditorModel().getReportDefinition();
    try {
      final WizardSpecification spec = wizardController.getEditorModel().getReportSpec();
      WizardProcessorUtil.applyWizardSpec( reportDefinition, spec );
      WizardProcessorUtil.ensureWizardProcessorIsAdded( reportDefinition, null );
    } catch ( Exception ex ) {
      throw new IllegalStateException();
    }

    if ( reportDefinition instanceof MasterReport ) {
      return WizardProcessorUtil.materialize( (MasterReport) reportDefinition, new WizardProcessor() );
    } else if ( reportDefinition instanceof SubReport ) {
      return WizardProcessorUtil.materialize( (SubReport) reportDefinition, new WizardProcessor() );
    } else {
      throw new IllegalStateException();
    }
  }

  private class CancelHandler implements PropertyChangeListener {
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( wizardController.isCancelled() ) {
        dialog.hide();
      }
    }
  }

  private class FinishedHandler implements PropertyChangeListener {
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( wizardController.isFinished() ) {
        dialog.hide();
      }
    }
  }
}
