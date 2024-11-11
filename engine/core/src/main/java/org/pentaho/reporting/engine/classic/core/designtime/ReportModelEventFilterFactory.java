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
