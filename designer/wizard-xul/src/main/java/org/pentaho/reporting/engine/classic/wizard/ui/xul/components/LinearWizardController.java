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

package org.pentaho.reporting.engine.classic.wizard.ui.xul.components;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessorUtil;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.Messages;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.WizardEditorModel;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.containers.XulDeck;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.awt.*;
import java.util.ArrayList;

/**
 * The wizard-controler manages the navigation between the wizard-panes. All panes are organized as a list, where each
 * panel cannot be enabled if the previous panels are not valid or enabled.
 * <p/>
 * It is possible to jump back to previous steps and change values there. In some cases, this will just update the
 * model, but in some cases this will invalidate the subsequent steps (for instance, if the query has been changed).
 *
 * @author William Seyler
 */
public class LinearWizardController extends AbstractXulEventHandler implements WizardController {


  // Binding converters
  private class BackButtonBindingConverter extends BindingConvertor<Integer, Boolean> {

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
     */
    @Override
    public Boolean sourceToTarget( final Integer value ) {
      return !( value > 0 );
    }

    /* (non-Javadoc)
     * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
     */
    @Override
    public Integer targetToSource( final Boolean value ) {
      return null;
    }

  }

  private static final Messages messages = Messages.getInstance();
  private static final String DISABLED_PROPERTY_NAME = "disabled"; //$NON-NLS-1$
  private static final String VALID_PROPERTY_NAME = "valid"; //$NON-NLS-1$
  private static final String NOT_DISABLED_PROPERTY = "!disabled"; //$NON-NLS-1$

  private static final String NEXT_BTN_ELEMENT_ID = "next_btn"; //$NON-NLS-1$
  private static final String BACK_BTN_ELEMENT_ID = "back_btn"; //$NON-NLS-1$
  private static final String FINISH_BTN_ELEMENT_ID = "finish_btn"; //$NON-NLS-1$
  private static final String CONTENT_DECK_ELEMENT_ID = "content_deck"; //$NON-NLS-1$

  private ArrayList<WizardStep> steps;
  private WizardEditorModel editorModel;
  private int activeStep = -1;  // bogus active step
  private boolean canceled;
  private boolean finished;

  private XulDomContainer mainXULContainer;

  private BindingFactory bf;
  private Binding nextButtonBinding, finishedButtonBinding; // previewButtonBinding;
  private DesignTimeContext designTimeContext;

  public LinearWizardController( final WizardEditorModel editorModel,
                                 final BindingFactory bf ) {
    this.steps = new ArrayList<WizardStep>();
    this.editorModel = editorModel;
    this.bf = bf;
  }

  /**
   * @param designTimeContext
   */
  public void setDesignTimeContext( final DesignTimeContext designTimeContext ) {
    this.designTimeContext = designTimeContext;
    for ( final WizardStep step : steps ) {
      step.setDesignTimeContext( designTimeContext );
    }
  }

  public WizardEditorModel getEditorModel() {
    return editorModel;
  }

  public void addStep( final AbstractWizardStep step ) {
    if ( step == null ) {
      throw new NullPointerException();
    }
    step.setEditorModel( editorModel );
    steps.add( step );
  }

  public void removeStep( final WizardStep step ) {
    steps.remove( step );
  }

  public WizardStep getStep( final int step ) {
    return steps.get( step );
  }

  public int getStepCount() {
    return steps.size();
  }

  public void setActiveStep( final int step ) {
    final int oldActiveStep = this.activeStep;
    if ( oldActiveStep >= 0 ) {
      final WizardStep deactivatingWizardStep = steps.get( oldActiveStep );
      if ( !deactivatingWizardStep.stepDeactivating() ) {
        DebugLog.log( deactivatingWizardStep.getStepName() + ": Canceled setActiveStep()" ); //$NON-NLS-1$
        return;
      }
    }

    this.activeStep = step;
    final WizardStep activatingWizardStep = steps.get( activeStep );
    updateBindings();
    activatingWizardStep.stepActivating();

    // update the controller panel
    final XulDeck deck = (XulDeck) mainXULContainer.getDocumentRoot().getElementById( CONTENT_DECK_ELEMENT_ID );
    deck.setSelectedIndex( activeStep );

    this.firePropertyChange( ACTIVE_STEP_PROPERTY_NAME, oldActiveStep, this.activeStep );
  }

  public int getActiveStep() {
    return activeStep;
  }

  public void initialize() {
    if ( steps.isEmpty() ) {
      throw new IllegalStateException(
        messages.getString( "LINEAR_WIZARD_CONTROLLER.Empty_Steps_Error" ) ); //$NON-NLS-1$
    }
    for ( final WizardStep wizardStep : steps ) {
      wizardStep.setBindings();
    }
    bf.setBindingType( Binding.Type.ONE_WAY );
    bf.createBinding( this, ACTIVE_STEP_PROPERTY_NAME, BACK_BTN_ELEMENT_ID, DISABLED_PROPERTY_NAME,
      new BackButtonBindingConverter() );
    //    bf.createBinding(this, ACTIVE_STEP_PROPERTY_NAME, STEP_PANEL_ELEMENT_ID, SELECTED_INDEX_PROPERTY_NAME);

    setActiveStep( 0 ); // Fires the events to update the buttons
    setCancelled( false );
    setFinished( false );
  }

  protected void updateBindings() {
    // Destroy any old bindings
    if ( nextButtonBinding != null ) {
      nextButtonBinding.destroyBindings();
    }
    if ( finishedButtonBinding != null ) {
      finishedButtonBinding.destroyBindings();
    }
    //    if (previewButtonBinding != null) {
    //      previewButtonBinding.destroyBindings();
    //    }

    // Create new binding to the current wizard panel
    bf.setBindingType( Binding.Type.ONE_WAY );
    nextButtonBinding =
      bf.createBinding( getStep( getActiveStep() ), VALID_PROPERTY_NAME, NEXT_BTN_ELEMENT_ID, NOT_DISABLED_PROPERTY );
    finishedButtonBinding =
      bf.createBinding( getStep( getActiveStep() ), FINISHABLE_PROPERTY_NAME, FINISH_BTN_ELEMENT_ID,
        NOT_DISABLED_PROPERTY );
    //    previewButtonBinding = bf.createBinding(getStep(getActiveStep()), PREVIEWABLE_PROPERTY_NAME,
    // PREVIEW_BTN_ELEMENT_ID, NOT_DISABLED_PROPERTY);


    try {
      nextButtonBinding.fireSourceChanged();
      finishedButtonBinding.fireSourceChanged();
      //      previewButtonBinding.fireSourceChanged();
    } catch ( Exception e ) {
      if ( designTimeContext == null ) {
        ExceptionDialog.showExceptionDialog( null, "Error", e.getMessage(), e );
      } else {
        designTimeContext.error( e );
      }
    }
  }

  public void cancel() {
    setCancelled( true );
    setFinished( false );
  }

  public void setCancelled( final boolean canceled ) {
    final boolean oldCanceled = this.canceled;
    this.canceled = canceled;
    this.firePropertyChange( CANCELLED_PROPERTY_NAME, oldCanceled, this.canceled );
  }

  public boolean isCancelled() {
    return canceled;
  }

  public void finish() {
    setFinished( true );
    setCancelled( false );
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished( final boolean finished ) {
    final boolean oldFinished = this.finished;
    this.finished = finished;
    this.firePropertyChange( FINISHED_PROPERTY_NAME, oldFinished, this.finished );
  }

  // Button click methods
  public void next() {
    setActiveStep( getActiveStep() + 1 );
  }

  public void back() {
    setActiveStep( getActiveStep() - 1 );
  }

  public void preview() {
    // At some point some parts of this should probably be XULified
    final PreviewDialog dialog;
    if ( designTimeContext != null ) {
      final Window window = designTimeContext.getParentWindow();
      if ( window instanceof Dialog ) {
        dialog = new PreviewDialog( (Dialog) window );
      } else if ( window instanceof Frame ) {
        dialog = new PreviewDialog( (Frame) window );
      } else {
        dialog = new PreviewDialog();
      }
    } else {
      dialog = new PreviewDialog();
    }

    dialog.setTitle( messages.getString( "LINEAR_WIZARD_CONTROLLER.Report_Preview" ) );
    dialog.setModal( false );

    try {
      final AbstractReportDefinition reportDefinition = editorModel.getReportDefinition();
      final AbstractReportDefinition element = (AbstractReportDefinition) reportDefinition.derive();
      final WizardSpecification spec = editorModel.getReportSpec();
      element.setAttribute( AttributeNames.Wizard.NAMESPACE, "enable", Boolean.TRUE );
      WizardProcessorUtil.applyWizardSpec( element, (WizardSpecification) spec.clone() );
      WizardProcessorUtil.ensureWizardProcessorIsAdded( element, null );

      if ( element instanceof MasterReport ) {
        dialog.setReportJob( (MasterReport) element );
      } else {
        final MasterReport report = new MasterReport();
        report.getReportHeader().addSubReport( (SubReport) element );
        dialog.setReportJob( report );
      }
      dialog.pack();
      LibSwingUtil.centerDialogInParent( dialog );
      dialog.setVisible( true );
    } catch ( Exception e ) {
      if ( designTimeContext != null ) {
        designTimeContext.error( e );
      } else {
        ExceptionDialog.showExceptionDialog( null, "Error", e.getMessage(), e );
      }
    }

  }

  // Stuff for XUL
  @Override
  public String getName() {
    return "wizard_controller"; //$NON-NLS-1$
  }

  public void onLoad() {
    DebugLog.log( "called onLoad()" ); //$NON-NLS-1$
    initialize();
  }

  /**
   * @param mainWizardContainer
   */
  public void registerMainXULContainer( final XulDomContainer mainWizardContainer ) {
    mainXULContainer = mainWizardContainer;
    bf.setDocument( mainWizardContainer.getDocumentRoot() );
  }

  public void setBindingFactory( final BindingFactory bf ) {
    this.bf = bf;
  }

  /* (non-Javadoc)
   * @see org.pentaho.reporting.engine.classic.wizard.ui.xul.components.WizardController#getBindingFactory()
   */
  public BindingFactory getBindingFactory() {
    return bf;
  }


}
