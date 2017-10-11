/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.validator;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.function.ElementTrafficLightFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ItemHideFunction;
import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.HashNMap;

import java.util.ArrayList;

public class DynamicReportStyleAnalyzer extends AbstractStructureVisitor {
  private HashNMap<String, StyleKey> styleByElementName;
  private HashNMap<InstanceID, StyleKey> styleById;
  private ArrayList<Section> rootBands;

  public DynamicReportStyleAnalyzer() {
    styleByElementName = new HashNMap<String, StyleKey>();
    styleById = new HashNMap<InstanceID, StyleKey>();
    rootBands = new ArrayList<Section>();
  }

  public void compute( final AbstractReportDefinition report ) {
    inspect( report );

    DynamicStyleRootBandAnalyzer rootBandAnalyzer = new DynamicStyleRootBandAnalyzer( styleByElementName, styleById );
    for ( Section element : rootBands ) {
      rootBandAnalyzer.compute( element );
    }
  }

  protected void inspectExpression( final AbstractReportDefinition report, final Expression expression ) {
    if ( expression instanceof RowBandingFunction ) {
      handleRowBanding( report, (RowBandingFunction) expression );
    }

    if ( expression instanceof ItemHideFunction ) {
      handleItemHide( (ItemHideFunction) expression );
    }

    if ( expression instanceof ElementTrafficLightFunction ) {
      handleTrafficLightFunction( (ElementTrafficLightFunction) expression );
    }
  }

  private void handleRowBanding( final AbstractReportDefinition report, final RowBandingFunction rb ) {
    String element = rb.getElement();
    if ( element == null ) {
      ItemBand itemBand = report.getItemBand();
      if ( itemBand != null ) {
        styleById.add( itemBand.getObjectID(), ElementStyleKeys.BACKGROUND_COLOR );
      }
    } else {
      styleByElementName.add( element, ElementStyleKeys.BACKGROUND_COLOR );
    }
  }

  private void handleItemHide( final ItemHideFunction rb ) {
    String element = rb.getElement();
    styleByElementName.add( element, ElementStyleKeys.VISIBLE );
  }

  private void handleTrafficLightFunction( final ElementTrafficLightFunction rb ) {
    String element = rb.getElement();
    if ( element == null ) {
      return;
    }
    if ( rb.isDefineBackground() ) {
      styleByElementName.add( element, ElementStyleKeys.BACKGROUND_COLOR );
    } else {
      styleByElementName.add( element, ElementStyleKeys.PAINT );
    }
  }

  protected void traverseSection( final Section section ) {
    traverseSectionWithoutSubReports( section );
  }

  protected void inspectElement( final ReportElement element ) {
    if ( element instanceof RootLevelBand && element instanceof Section ) {
      rootBands.add( (Section) element );
    }
  }
}
