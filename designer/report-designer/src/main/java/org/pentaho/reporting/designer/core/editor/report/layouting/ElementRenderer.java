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

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface ElementRenderer {
  public void setVisualHeight( final double visualHeight );

  public double getVisualHeight();

  public long[] getHorizontalEdgePositionKeys();

  public ElementType getElementType();

  public InstanceID getRepresentationId();

  public boolean isHideInLayout();

  public void addChangeListener( ChangeListener changeListener );

  public void removeChangeListener( ChangeListener changeListener );

  public LinealModel getVerticalLinealModel();

  public double getLayoutHeight();

  public Rectangle2D getBounds();

  public boolean draw( Graphics2D g2 );

  public void handleError( ReportDesignerContext designerContext,
                           ReportDocumentContext reportContext );

  public StrictBounds getRootElementBounds();

  Section getElement();

  Element[] getElementsAt( double x, double y, double width, double height );

  Element[] getElementsAt( double x, double y );

  BreakPositionsList getHorizontalEdgePositions();

  BreakPositionsList getVerticalEdgePositions();

  void invalidateLayout();

  void dispose();
}
