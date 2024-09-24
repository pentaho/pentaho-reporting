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

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class CrosstabCellStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private static final Float ZERO = new Float( 0 );

  public CrosstabCellStyleSheet() {
    this.parent = BandDefaultStyleSheet.getBandDefaultStyle();
  }

  public StyleSheet getParent() {
    return parent;
  }

  public InstanceID getId() {
    return parent.getId();
  }

  public long getChangeTracker() {
    return parent.getChangeTracker();
  }

  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    if ( ElementStyleKeys.MIN_WIDTH.equals( key ) ) {
      // this is *auto* mode
      return ZERO;
    }
    if ( ElementStyleKeys.VALIGNMENT.equals( key ) ) {
      return ElementAlignment.TOP;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()] = ZERO;
    objects[ElementStyleKeys.VALIGNMENT.getIdentifier()] = ElementAlignment.TOP;
    return objects;
  }
}
