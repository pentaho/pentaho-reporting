/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.wizard.ui.xul.components;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.ui.xul.binding.BindingFactory;

public interface WizardController {
  public static final String ACTIVE_STEP_PROPERTY_NAME = "activeStep"; //$NON-NLS-1$

  public static final String STEP_COUNT_PROPERTY_NAME = "stepCount"; //$NON-NLS-1$

  public static final String CANCELLED_PROPERTY_NAME = "cancelled"; //$NON-NLS-1$

  public static final String FINISHED_PROPERTY_NAME = "finished"; //$NON-NLS-1$

  public static final String FINISHABLE_PROPERTY_NAME = "finishable"; //$NON-NLS-1$

  public static final String PREVIEWABLE_PROPERTY_NAME = "previewable"; //$NON-NLS-1$

  public WizardStep getStep( int step );

  public int getStepCount();

  public void setActiveStep( int step );

  public int getActiveStep();

  public void initialize();

  public void cancel();

  public void finish();

  public boolean isFinished();

  public boolean isCancelled();

  public void setBindingFactory( BindingFactory factory );

  public BindingFactory getBindingFactory();

  public void setDesignTimeContext( DesignTimeContext context );
}
