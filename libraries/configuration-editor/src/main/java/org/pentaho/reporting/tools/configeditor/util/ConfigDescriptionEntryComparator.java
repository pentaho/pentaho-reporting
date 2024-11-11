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


package org.pentaho.reporting.tools.configeditor.util;

import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

import java.util.Comparator;

public class ConfigDescriptionEntryComparator implements Comparator<ConfigDescriptionEntry> {
  public ConfigDescriptionEntryComparator() {
  }

  public int compare( final ConfigDescriptionEntry e1, final ConfigDescriptionEntry e2 ) {
    if ( e1 == null ) {
      return 1;
    }
    if ( e2 == null ) {
      return -1;
    }
    if ( e1 == e2 ) {
      return 0;
    }
    return e1.getKeyName().compareTo( e2.getKeyName() );
  }
}
