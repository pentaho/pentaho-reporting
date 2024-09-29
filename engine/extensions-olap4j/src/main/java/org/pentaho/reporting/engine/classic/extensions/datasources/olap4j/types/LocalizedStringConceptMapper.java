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
