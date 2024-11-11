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


package org.pentaho.reporting.engine.classic.core;

import java.awt.Image;

/**
 * The LocalImageContainer makes the image available as 'java.awt.Image' instance. This way, the image can be included
 * in the local content creation process.
 *
 * @author Thomas Morgner
 */
public interface LocalImageContainer extends ImageContainer {
  /**
   * Returns the image instance for this image container. This method might return <code>null</code>, if the image is
   * not available.
   *
   * @return the image data.
   */
  public Image getImage();

  /**
   * Returns the name of this image reference. The name returned should be unique.
   *
   * @return the name.
   */
  public String getName();

  /**
   * Checks whether this image has a assigned identity. Two identities should be equal, if the image contents are equal.
   *
   * @return true, if that image contains contains identity information, false otherwise.
   */
  public boolean isIdentifiable();

  /**
   * Returns the identity information.
   *
   * @return the image identifier.
   */
  public Object getIdentity();
}
