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
