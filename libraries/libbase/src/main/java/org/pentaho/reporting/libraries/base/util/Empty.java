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


package org.pentaho.reporting.libraries.base.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Class which holds a static reference to a set of empty objects. This is created for performance reasons. Using this
 * class will prevent creating duplicated "empty" object.
 *
 * @author David Kincade
 */
public final class Empty {

  /**
   * No reason to create an instance of this class.
   */
  private Empty() {
  }

  /**
   * The empty string.
   */
  public static final String STRING = "";

  /**
   * An empty array of Strings.
   */
  public static final String[] STRING_ARRAY = new String[ 0 ];

  /**
   * An empty Map. (Collections.EMPTY_MAP is not available until JDK 1.4)
   *
   * @deprecated this is a redeclaration of the Collections.EMPTY_MAP field and should be killed.
   */
  @SuppressWarnings( "PublicStaticCollectionField" )
  public static final Map MAP = Collections.EMPTY_MAP;

  /**
   * An empty List.
   *
   * @noinspection PublicStaticCollectionField
   * @deprecated this is a redeclaration of the Collections.EMPTY_LIST field and should be killed.
   */
  public static final List LIST = Collections.EMPTY_LIST;
}
