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


package org.pentaho.reporting.libraries.css.model;

import org.pentaho.reporting.libraries.css.UnmodifiableStyleSheetException;
import org.pentaho.reporting.libraries.css.selectors.CSSSelector;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 23.11.2005, 10:59:26
 *
 * @author Thomas Morgner
 */
public class CSSStyleRule extends CSSDeclarationRule {
  private CSSSelector selector;

  public CSSStyleRule( final StyleSheet parentStyle,
                       final StyleRule parentRule ) {
    super( parentStyle, parentRule );
  }

  public CSSSelector getSelector() {
    return selector;
  }

  public void setSelector( final CSSSelector selector ) {
    if ( isReadOnly() ) {
      throw new UnmodifiableStyleSheetException();
    }
    this.selector = selector;
  }

  public void merge( final CSSStyleRule elementRule ) {
    if ( elementRule.isEmpty() ) {
      return;
    }

    final boolean[] importantFlags = elementRule.getImportantValues();
    final CSSValue[] values = elementRule.getStyleValues();
    final StyleKey[] keys = elementRule.getPropertyKeysAsArray();
    for ( int i = 0; i < values.length; i++ ) {
      final CSSValue cssValue = values[ i ];
      if ( cssValue != null ) {
        final StyleKey propertyName = keys[ i ];
        setPropertyValue( propertyName, cssValue, importantFlags[ i ] && isImportant( propertyName ) );
      }
    }
  }
}
