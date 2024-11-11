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


package org.pentaho.reporting.engine.classic.wizard.ui.xul.components;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.dom.Document;

/**
 * A single step in the wizard. The architecture assumes that the wizard-ui keeps synchronized with the model at all the
 * time, so that other steps can react to changes and update their own availability. Steps *should* preserve the user
 * input even when they temporarily enter a invalid UI state while they are not yet active.
 *
 * @author Thomas Morgner
 */
public interface WizardStep {


  /**
   * setBindings()
   * <p/>
   * Allows concrete implementations to set their bindings for enclosed properties and Xul defined elements.
   */
  public void setBindings();

  /**
   * Checks, whether the step is currently valid. A step is valid, if it
   *
   * @return true, if the model matches the step's internal state, false otherwise.
   */
  public boolean isValid();

  /**
   * stepActivating()
   * <p/>
   * Called on each step just before it become active (before it's card is shown).
   */
  public void stepActivating();

  /**
   * Called on a step just before it becomes deactivated (before the new active step is shown).
   *
   * @return boolean indicating that this step should be allowed to become deactive
   */
  public boolean stepDeactivating();

  /**
   * setFinishable()
   *
   * @param finishable sets the flag that determines if the "Finish" button should be enabled for this wizard panel.
   */
  public void setFinishable( boolean finishable );

  /**
   * @return a boolean that determines if the "Finish" button should be enabled.
   */
  public boolean isFinishable();

  /**
   * setPreviewable()
   *
   * @param previewable Sets the previewable field to the value of previewable.  Determines if the "Preview" button
   *                    should be enabled.
   */
  public void setPreviewable( boolean previewable );


  /**
   * @return a boolean the indicates if the report can be previewed at this point
   */
  public boolean isPreviewable();

  /**
   * @param mainWizardContainer
   * @throws XulException Creates the presentation layer associated with this WizardStep.  This is usually done by
   *                      loading an overlay into the main_wzard_panel.xul
   */
  public void createPresentationComponent( XulDomContainer mainWizardContainer ) throws XulException;


  public void setBindingFactory( BindingFactory bindingFactory );

  public void setDocument( Document document );

  /**
   * @return a string (must be localized) that describes this step
   */
  public String getStepName();

  public void setDesignTimeContext( DesignTimeContext context );

}
