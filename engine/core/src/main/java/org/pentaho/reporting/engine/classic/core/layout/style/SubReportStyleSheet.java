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

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class SubReportStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private Boolean pagebreakBefore;
  private Boolean pagebreakAfter;

  public SubReportStyleSheet( final boolean pagebeakBefore, final boolean pagebreakAfter ) {
    this.parent = ElementDefaultStyleSheet.getDefaultStyle();
    this.pagebreakAfter = pagebreakAfter;
    this.pagebreakBefore = pagebeakBefore;
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
    if ( BandStyleKeys.PAGEBREAK_AFTER.equals( key ) ) {
      return pagebreakAfter;
    }
    if ( BandStyleKeys.PAGEBREAK_BEFORE.equals( key ) ) {
      return pagebreakBefore;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[BandStyleKeys.PAGEBREAK_AFTER.getIdentifier()] = pagebreakAfter;
    objects[BandStyleKeys.PAGEBREAK_BEFORE.getIdentifier()] = pagebreakBefore;
    return objects;
  }
}
