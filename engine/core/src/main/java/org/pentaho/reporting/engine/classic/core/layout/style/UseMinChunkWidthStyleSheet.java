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

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 12.08.2007, 18:32:30
 *
 * @author Thomas Morgner
 */
public class UseMinChunkWidthStyleSheet extends AbstractStyleSheet {
  private StyleSheet parent;
  private Boolean useMinChunkWidth;

  public UseMinChunkWidthStyleSheet( final boolean useMinChunkWidth ) {
    this.parent = BandDefaultStyleSheet.getBandDefaultStyle();
    if ( useMinChunkWidth ) {
      this.useMinChunkWidth = Boolean.TRUE;
    } else {
      this.useMinChunkWidth = Boolean.FALSE;
    }
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
    if ( ElementStyleKeys.USE_MIN_CHUNKWIDTH.equals( key ) ) {
      return useMinChunkWidth;
    }
    return parent.getStyleProperty( key, defaultValue );
  }

  public Object[] toArray() {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.USE_MIN_CHUNKWIDTH.getIdentifier()] = useMinChunkWidth;
    return objects;
  }
}
