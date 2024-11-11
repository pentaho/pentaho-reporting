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


package org.pentaho.reporting.engine.classic.core.layout.style;

import java.util.List;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class ParagraphPoolboxStyleSheet extends AbstractStyleSheet {
  private StyleSheet parentStyleSheet;
  private StyleSheet defaultStyleSheet;

  public ParagraphPoolboxStyleSheet( final StyleSheet parentStyleSheet ) {
    if ( parentStyleSheet == null ) {
      throw new NullPointerException();
    }
    this.parentStyleSheet = parentStyleSheet;
    this.defaultStyleSheet = ElementDefaultStyleSheet.getDefaultStyle();
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE.equals( key ) ) {
      return Boolean.TRUE;
    }

    if ( key.isInheritable() ) {
      return parentStyleSheet.getStyleProperty( key, defaultValue );
    }
    return defaultStyleSheet.getStyleProperty( key, defaultValue );
  }

  public StyleSheet getParent() {
    return parentStyleSheet;
  }

  public InstanceID getId() {
    return parentStyleSheet.getId();
  }

  public long getChangeTracker() {
    return parentStyleSheet.getChangeTracker();
  }

  public Object[] toArray() {
    final Object[] objects = defaultStyleSheet.toArray();
    final List<StyleKey> keys = StyleKey.getDefinedStyleKeysList();
    for ( int i = 0, len = keys.size(); i < len; i++ ) {
      final StyleKey key = keys.get( i );
      if ( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE.equals( key ) ) {
        objects[ i ] = Boolean.TRUE;
      } else if ( key.isInheritable() ) {
        objects[ i ] = parentStyleSheet.getStyleProperty( key, null );
      }
    }
    return objects;
  }
}
