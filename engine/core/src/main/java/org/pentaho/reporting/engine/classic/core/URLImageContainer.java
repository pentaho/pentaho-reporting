/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
