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

package org.pentaho.reporting.designer.layout;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.DesignerPageDrawable;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.engine.classic.core.Band;

public class TestRootBandRenderer extends RootBandRenderer {
  public TestRootBandRenderer( final Band visualReportElement,
                               final ReportRenderContext renderContext ) {
    super( visualReportElement, renderContext );
  }

  public DesignerPageDrawable getLogicalPageDrawable() {
    return super.getLogicalPageDrawable();
  }
}
