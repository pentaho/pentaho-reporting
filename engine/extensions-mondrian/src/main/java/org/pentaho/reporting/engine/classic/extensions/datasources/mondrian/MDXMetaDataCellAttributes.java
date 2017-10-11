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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.olap.Cell;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class MDXMetaDataCellAttributes implements DataAttributes {
  private static final String[] NAMESPACES =
    new String[] { MDXMetaAttributeNames.NAMESPACE };
  private static final String[] PROPERTIES =
    new String[]
      {
        MDXMetaAttributeNames.BACKGROUND_COLOR,
        MDXMetaAttributeNames.FOREGROUND_COLOR,

        MDXMetaAttributeNames.FONT_FLAGS,
        MDXMetaAttributeNames.FONTSIZE,

        MDXMetaAttributeNames.FONTNAME,
        MDXMetaAttributeNames.FORMAT_STRING,
        MDXMetaAttributeNames.LANGUAGE,
      };

  private DataAttributes backend;
  private Cell cell;

  public MDXMetaDataCellAttributes( final DataAttributes backend,
                                    final Cell cell ) {
    ArgumentNullException.validate( "cell", cell );
    ArgumentNullException.validate( "backend", backend );

    this.cell = cell;
    this.backend = backend;
  }

  public String[] getMetaAttributeDomains() {
    final String[] backendDomains = backend.getMetaAttributeDomains();
    return StringUtils.merge( NAMESPACES, backendDomains );
  }

  public String[] getMetaAttributeNames( final String domainName ) {
    if ( MDXMetaAttributeNames.NAMESPACE.equals( domainName ) ) {
      return PROPERTIES.clone();
    }

    return backend.getMetaAttributeNames( domainName );
  }

  public Object getMetaAttribute( final String domain, final String name, final Class type,
                                  final DataAttributeContext context ) {
    return getMetaAttribute( domain, name, type, context, null );
  }

  public Object getMetaAttribute( final String domain,
                                  final String name,
                                  final Class type,
                                  final DataAttributeContext context,
                                  final Object defaultValue ) {

    if ( MDXMetaAttributeNames.NAMESPACE.equals( domain ) ) {
      final Object attribute = cell.getPropertyValue( name );
      if ( attribute == null ) {
        return defaultValue;
      }

      return attribute;
    }

    return backend.getMetaAttribute( domain, name, type, context, defaultValue );
  }

  public Object clone() throws CloneNotSupportedException {
    final MDXMetaDataCellAttributes attributes = (MDXMetaDataCellAttributes) super.clone();
    attributes.backend = (DataAttributes) backend.clone();
    return attributes;
  }

  public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
    return DefaultConceptQueryMapper.INSTANCE;
  }
}
