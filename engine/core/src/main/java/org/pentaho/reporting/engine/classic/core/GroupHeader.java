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


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupHeaderType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

/**
 * A report band that appears at the beginning of each instance of a group. A group-header can be marked as repeating
 * header causing the header to be printed at the top of each page as long as the group is active. If the header is
 * marked sticky, the header will even be printed for all sub-report pages.
 * <p/>
 * Subreports defined for a repeating group header will be ignored for all repeating instances.
 *
 * @author David Gilbert
 */
public class GroupHeader extends AbstractRootLevelBand {
  /**
   * Constructs a group header band, containing no elements.
   */
  public GroupHeader() {
    setElementType( new GroupHeaderType() );
  }

  /**
   * Checks whether this group header should be repeated on new pages.
   *
   * @return true, if the header will be repeated, false otherwise
   */
  public boolean isRepeat() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER );
  }

  /**
   * Defines, whether this group header should be repeated on new pages.
   *
   * @param repeat
   *          true, if the header will be repeated, false otherwise
   */
  public void setRepeat( final boolean repeat ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER, repeat );
    notifyNodePropertiesChanged();
  }

  /**
   * Returns true if the footer should be shown on all subreports.
   *
   * @return true or false.
   */
  public boolean isSticky() {
    return getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY, false );
  }

  /**
   * Defines whether the footer should be shown on all subreports.
   *
   * @param b
   *          a flag indicating whether or not the footer is shown on the first page.
   */
  public void setSticky( final boolean b ) {
    getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, b );
    notifyNodePropertiesChanged();
  }

}
