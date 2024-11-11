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


package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;

import java.awt.*;

public interface DrillDownUi {
  public Component getEditorPanel();

  public DrillDownModel getModel();

  public void init( final Component parent,
                    final ReportDesignerContext reportDesignerContext,
                    final DrillDownModel model,
                    final String[] extraFields ) throws DrillDownUiException;

  public void deactivate();
}
