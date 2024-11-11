/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

import java.util.Locale;

public class LocalizedStringConceptMapper implements ConceptQueryMapper {
  public static final ConceptQueryMapper INSTANCE = new LocalizedStringConceptMapper();
  private static final Log logger = LogFactory.getLog( LocalizedStringConceptMapper.class );

  public LocalizedStringConceptMapper() {
  }

  /**
   * @param value
   * @param type
   * @return
   */
  public Object getValue( final Object value, final Class type, final DataAttributeContext context ) {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof LocalizedString == false ) {
      return null;
    }

    if ( type == null || Object.class.equals( type ) || LocalizedString.class.equals( type ) ) {
      if ( value instanceof LocalizedStringWrapper ) {
        return value;
      }
      return new LocalizedStringWrapper( (LocalizedString) value );
    }

    if ( String.class.equals( type ) == false ) {
      return null;
    }

    final LocalizedString settings = (LocalizedString) value;
    final Locale locale = context.getLocale();
    final String localeAsText = locale.toString();
    final Object o = settings.getLocalizedString( localeAsText );
    if ( o == null ) {
      logger.warn( "Unable to translate localized-string property for locale [" + locale + "]. "
        + "The localization does not contain a translation for this locale and does not provide a fallback." );
    }
    return o;
  }
}
