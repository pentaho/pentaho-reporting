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

package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;

public class ReportModelEventFilterFactory {
  public static class StyleReportModelEventFilter implements ReportModelEventFilter {
    private StyleKey key;

    public StyleReportModelEventFilter( final StyleKey key ) {
      this.key = key;
    }

    public boolean isFilteredEvent( final ReportModelEvent event ) {
      final Object parameter = event.getParameter();
      if ( parameter instanceof StyleChange == false ) {
        return false;
      }
      final StyleChange styleChange = (StyleChange) parameter;
      return this.key.equals( styleChange.getStyleKey() );
    }
  }

  public static class AttributeReportModelEventFilter implements ReportModelEventFilter {
    private String nameSpace;
    private String name;

    public AttributeReportModelEventFilter( final String nameSpace, final String name ) {
      this.name = name;
      this.nameSpace = nameSpace;
    }

    public boolean isFilteredEvent( final ReportModelEvent event ) {
      final Object parameter = event.getParameter();
      if ( parameter instanceof AttributeChange == false ) {
        return false;
      }
      final AttributeChange attrChange = (AttributeChange) parameter;
      if ( nameSpace.equals( attrChange.getNamespace() ) == false ) {
        return false;
      }
      if ( name.equals( attrChange.getName() ) == false ) {
        return false;
      }
      return true;
    }
  }

  public ReportModelEventFilterFactory() {
  }

  public ReportModelEventFilter createStyleFilter( final StyleKey key ) {
    return new StyleReportModelEventFilter( key );
  }

  public ReportModelEventFilter createAttributeFilter( final String attrNameSpace, final String attrName ) {
    return new AttributeReportModelEventFilter( attrNameSpace, attrName );
  }

}
