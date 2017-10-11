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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.util.Locale;

public class LocalizedStringConceptMapper implements ConceptQueryMapper {
  private static final Log logger = LogFactory.getLog( LocalizedStringConceptMapper.class );

  public LocalizedStringConceptMapper() {
  }

  public Object getValue( final Object value, final Class type, final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof LocalizedString == false ) {
      return null;
    }

    if ( type == null || LocalizedString.class.equals( type ) ) {
      return value;
    }

    if ( String.class.equals( type ) == false ) {
      return null;
    }

    final LocalizedString settings = (LocalizedString) value;
    final Locale locale = context.getLocale();
    final Object o = settings.getValue( locale );
    if ( o == null ) {
      logger.warn( "Unable to translate localized-string property for locale [" + locale + "]. " +
        "The localization does not contain a translation for this locale and does not provide a fallback." );
    }
    return o;
  }
}
