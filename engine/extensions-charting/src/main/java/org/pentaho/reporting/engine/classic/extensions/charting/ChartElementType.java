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

package org.pentaho.reporting.engine.classic.extensions.charting;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;

public class ChartElementType extends ContentType {
  private transient ElementMetaData elementType;

  public ChartElementType() {
  }

  public ElementMetaData getMetaData() {
    if ( elementType == null ) {
      elementType = ElementTypeRegistry.getInstance().getElementType( "pentaho-chartbeans" );
    }
    return elementType;
  }

  protected Object queryChartValue( final ReportElement element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object attribute = element.getAttribute( ChartingModule.NAMESPACE, "chart-definition" );
    if ( attribute != null ) {
      return attribute;
    }
    return null;
  }


  public Object getValue( final ExpressionRuntime runtime,
                          final ReportElement element ) {
    Object rawValue = queryChartValue( element );
    if ( rawValue instanceof String ) {
      try {
        rawValue = String.valueOf( rawValue ).getBytes( "UTF-8" );
      } catch ( UnsupportedEncodingException e ) {
        e.printStackTrace();
      }
    } else {
      rawValue = ElementTypeUtils.queryFieldOrValue( runtime, element );
      if ( rawValue == null ) {
        return filter( runtime, element,
          element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ) );
      }
    }

    try {
      final ResourceManager resourceManager = runtime.getProcessingContext().getResourceManager();
      final ResourceKey chartKey;
      if ( rawValue instanceof String ) {
        chartKey = resourceManager.deriveKey( runtime.getProcessingContext().getContentBase(), (String) rawValue );
      } else {
        chartKey = resourceManager.createKey( rawValue );
      }

      final Resource res = resourceManager.create( chartKey, null, Document.class );

      return null;
    } catch ( Throwable e ) {
      return filter( runtime, element,
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE ) );
    }
  }
}
