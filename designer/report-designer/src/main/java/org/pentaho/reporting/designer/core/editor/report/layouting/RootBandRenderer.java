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
import org.pentaho.reporting.engine.classic.core.Band;

/**
 * This class only exists to ensure type-safety. It guarantees that the edited element is a band.
 *
 * @author Thomas Morgner
 */
public class RootBandRenderer extends AbstractElementRenderer {
  public RootBandRenderer( final Band visualReportElement,
                           final ReportDocumentContext renderContext ) {
    super( visualReportElement, renderContext );
  }
}
