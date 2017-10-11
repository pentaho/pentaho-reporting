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

package org.pentaho.reporting.engine.classic.core.designtime.compat;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class LayoutCompatibility_3_9_Converter extends AbstractCompatibilityConverter {
  public LayoutCompatibility_3_9_Converter() {
  }

  public int getTargetVersion() {
    return ClassicEngineBoot.computeVersionId( 3, 9, 0 );
  }

  public void inspectElement( final ReportElement element ) {
    element.getStyle().setStyleProperty( ElementStyleKeys.WIDOWS, null );
    element.getStyle().setStyleProperty( ElementStyleKeys.ORPHANS, null );
    element.getStyle().setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, null );
    if ( element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.CONTROL
        || element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.SUBREPORT ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, null );
    }

    if ( element.getMetaData().getReportElementType() == ElementMetaData.TypeClassification.CONTROL ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.WIDTH, null );
      element.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, null );

      element.setStyleExpression( ElementStyleKeys.MIN_WIDTH, null );
      element.setStyleExpression( ElementStyleKeys.MIN_HEIGHT, null );
      element.setStyleExpression( ElementStyleKeys.MAX_WIDTH, null );
      element.setStyleExpression( ElementStyleKeys.MAX_HEIGHT, null );
      element.setStyleExpression( ElementStyleKeys.WIDTH, null );
      element.setStyleExpression( ElementStyleKeys.HEIGHT, null );
    }
  }
}
