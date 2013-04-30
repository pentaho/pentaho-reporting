/*
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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report.elements;

import java.util.Locale;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.AbstractReportElementDragHandler;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

public class BandReportElementDragHandler extends AbstractReportElementDragHandler
{
  public BandReportElementDragHandler()
  {
  }

  protected Element createElement(final ElementMetaData elementMetaData,
                                  final String fieldName,
                                  final ReportRenderContext context) throws InstantiationException
  {
    final ElementType type = elementMetaData.create();
    final Element visualElement = new Band();
    type.configureDesignTimeDefaults(visualElement, Locale.getDefault());

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty(ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH);
    styleSheet.setStyleProperty(ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT);
    return visualElement;
  }
}