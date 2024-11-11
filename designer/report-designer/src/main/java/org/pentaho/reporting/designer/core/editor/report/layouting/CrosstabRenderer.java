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


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;

public class CrosstabRenderer extends AbstractElementRenderer {
  public CrosstabRenderer( final CrosstabGroup group,
                           final ReportDocumentContext renderContext ) {
    super( group, renderContext );
  }

  public CrosstabGroup getCrosstabGroup() {
    return (CrosstabGroup) getElement();
  }
}
