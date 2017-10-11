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
