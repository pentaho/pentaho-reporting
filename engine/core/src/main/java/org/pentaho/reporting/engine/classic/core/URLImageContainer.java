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

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.net.URL;

/**
 * An image container, that references a remote image. The image is not required to be loadable as long as this
 * container returns all necessary information correctly.
 * <p/>
 * Some output targets will not display anything, if the image is not loadable.
 *
 * @author Thomas Morgner
 */
public interface URLImageContainer extends ImageContainer {
  /**
   * Returns the resourcekey that was used to load the image.
   *
   * @return the resource key.
   */
  public ResourceKey getResourceKey();

  /**
   * Returns the source URL, if available.
   *
   * @return the source URL of the image.
   */
  public URL getSourceURL();

  /**
   * Returns the source URL as string. This could also be a relative URL which is not readable by the report processor,
   * the source URL string is copied as is - without being interpreted by the output target.
   *
   * @return the source URL as string.
   */
  public String getSourceURLString();

  /**
   * Defines, whether the given URLs are readable. If there is no java.net.URL sourceURL, then this method must return
   * false.
   *
   * @return true, if the source URL is loadable, false otherwise.
   */
  public boolean isLoadable();
}
