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


package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.style.StyleKey;

import java.beans.PropertyEditor;

/**
 * Information about the style-keys of an element. In general all elements can carry all style information, but some
 * styles are simply not applicable to elements, and some are optional on some element types.
 *
 * @author Thomas Morgner
 */
public interface StyleMetaData extends MetaData {
  public StyleKey getStyleKey();

  public PropertyEditor getEditor();

  public Class getTargetType();

}
