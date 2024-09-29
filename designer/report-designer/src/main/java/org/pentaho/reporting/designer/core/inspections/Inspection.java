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


package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface Inspection {
  /**
   * The inspection is cheap enough to be run constantly while editing.
   *
   * @return true, if it can run while the editing is running, false otherwise.
   */
  public boolean isInlineInspection();

  public void inspect( final ReportDesignerContext designerContext,
                       final ReportDocumentContext reportRenderContext,
                       final InspectionResultListener resultHandler ) throws ReportDataFactoryException;
}
