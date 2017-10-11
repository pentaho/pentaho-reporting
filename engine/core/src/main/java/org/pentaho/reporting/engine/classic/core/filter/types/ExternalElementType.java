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

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.Locale;

public class ExternalElementType extends AbstractElementType {
  public static final ElementType INSTANCE = new ExternalElementType();
  private static final Log logger = LogFactory.getLog( ExternalElementType.class );

  public ExternalElementType() {
    super( "external-element-field" );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {

  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object staticValue = ElementTypeUtils.queryStaticValue( element );
    if ( staticValue != null ) {
      return staticValue;
    }
    return ElementTypeUtils.queryFieldName( element );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element for which the data is computed.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object value = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( value != null ) {
      final Object filteredValue = filter( runtime, element, value );
      if ( filteredValue != null ) {
        return filteredValue;
      }
    }
    final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
    return filter( runtime, element, nullValue );
  }

  private Object filter( final ExpressionRuntime runtime, final ReportElement element, final Object value ) {
    if ( value instanceof Element ) {
      return value;
    }

    try {
      final ResourceKey contentBase = runtime.getProcessingContext().getContentBase();
      final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
      final Object contentBaseValue =
          element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE );
      final ResourceKey key = resManager.createOrDeriveKey( contentBase, value, contentBaseValue );
      if ( key == null ) {
        return null;
      }

      final Object targetRaw = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.TARGET_TYPE );
      final Class target;
      if ( targetRaw instanceof String ) {
        final ClassLoader loader = ObjectUtilities.getClassLoader( ExternalElementType.class );
        target = Class.forName( (String) targetRaw, false, loader );

        if ( target == null ) {
          return null;
        }
      } else {
        target = SubReport.class;
      }

      final Resource resource = resManager.create( key, contentBase, target );
      final Object resourceContent = resource.getResource();
      if ( resourceContent instanceof Element ) {
        return resourceContent;
      }
    } catch ( Exception e ) {
      logger.warn( "Failed to load content using value " + value, e );
    }
    return null;
  }
}
