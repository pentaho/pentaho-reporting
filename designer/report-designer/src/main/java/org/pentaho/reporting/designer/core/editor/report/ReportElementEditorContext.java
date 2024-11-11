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


package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.Element;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface ReportElementEditorContext {
  public Point2D normalize( final Point2D p );

  public ReportDesignerContext getDesignerContext();

  public ReportDocumentContext getRenderContext();

  public JComponent getRepresentationContainer();

  public Element getElementForLocation( final Point2D normalizedPoint,
                                        final boolean onlySelected );

  public Element getDefaultElement();
}
