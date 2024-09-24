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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.designtime.swing.colorchooser.ColorSchema;

import java.awt.*;
import java.util.LinkedHashSet;

public class ReportColorSchema implements ColorSchema {
  private LinkedHashSet<Color> colors;

  public ReportColorSchema( final AbstractReportDefinition reportDefinition ) {
    colors = new LinkedHashSet<Color>();
    collectColors( reportDefinition );
  }

  private void collectColors( final AbstractReportDefinition def ) {
    final Expression[] expressions = def.getExpressions().getExpressions();
    for ( final Expression expression : expressions ) {
      collectColorFromExpressions( expression );
    }
    collectColorFromSection( def );
  }

  private void collectColorFromExpressions( final Expression expression ) {
    try {
      final BeanUtility beanUtility = new BeanUtility( expression );
      final String[] propertyNames = beanUtility.getProperties();

      for ( int i = 0; i < propertyNames.length; i++ ) {
        try {
          final String key = propertyNames[ i ];
          // filter some of the standard properties. These are system-properties
          // and are set elsewhere
          final Class propertyType = beanUtility.getPropertyType( key );
          if ( Color.class.isAssignableFrom( propertyType ) == false ) {
            continue;
          }

          final Object property = beanUtility.getProperty( key );
          if ( property instanceof Color ) {
            colors.add( (Color) property );
          }
        } catch ( Exception e ) {
          // ignore
        }
      }
    } catch ( Exception e ) {
      // ignore
    }
  }

  private void collectColorFromSection( final Section section ) {
    collectColorFromElement( section );
    for ( final ReportElement element : section ) {
      if ( element instanceof Section ) {
        collectColorFromSection( (Section) element );
      } else {
        collectColorFromElement( element );
      }
    }

    if ( section instanceof RootLevelBand ) {
      final RootLevelBand b = (RootLevelBand) section;
      final SubReport[] subReports = b.getSubReports();
      for ( final SubReport subReport : subReports ) {
        collectColorFromSection( subReport );
      }
    }
  }

  private void collectColorFromElement( final ReportElement element ) {
    final ElementStyleSheet style = element.getStyle();
    final StyleKey[] propertyKeys = style.getPropertyKeys();
    for ( final StyleKey propertyKey : propertyKeys ) {
      if ( Color.class.isAssignableFrom( propertyKey.getValueType() ) ) {
        final Color styleProperty = (Color) style.getStyleProperty( propertyKey );
        if ( styleProperty != null ) {
          colors.add( styleProperty );
        }

        final Expression styleExpression = element.getStyleExpression( propertyKey );
        if ( styleExpression != null ) {
          collectColorFromExpressions( styleExpression );
        }
      }
    }

    final AttributeMetaData[] attributeDescriptions = element.getMetaData().getAttributeDescriptions();
    for ( final AttributeMetaData attributeMetaData : attributeDescriptions ) {
      final Class targetType = attributeMetaData.getTargetType();
      if ( Color.class.isAssignableFrom( targetType ) ) {
        final Color styleProperty = (Color)
          element.getAttribute( attributeMetaData.getNameSpace(), attributeMetaData.getName() );
        if ( styleProperty != null ) {
          colors.add( styleProperty );
        }
        final Expression styleExpression =
          element.getAttributeExpression( attributeMetaData.getNameSpace(), attributeMetaData.getName() );
        if ( styleExpression != null ) {
          collectColorFromExpressions( styleExpression );
        }

      } else if ( Expression.class.isAssignableFrom( targetType ) ) {
        final Expression styleProperty = (Expression)
          element.getAttribute( attributeMetaData.getNameSpace(), attributeMetaData.getName() );
        if ( styleProperty != null ) {
          collectColorFromExpressions( styleProperty );
        }
      }
    }
  }

  public Color[] getColors() {
    return colors.toArray( new Color[ colors.size() ] );
  }

  public String getName() {
    return UtilMessages.getInstance().getString( "ReportColorSchema.Title" );
  }
}
