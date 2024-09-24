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
