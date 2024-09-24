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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

/**
 * The ContentNotRecognizedException signals that none of the selected resource factories was able to handle the
 * request.
 *
 * @author Thomas Morgner
 */
public class ContentNotRecognizedException extends ResourceCreationException {
  private static final long serialVersionUID = 816828118909665976L;

  /**
   * Creates a ContentNotRecognizedException  with no message and no parent.
   */
  public ContentNotRecognizedException() {
  }

  /**
   * Creates an ContentNotRecognizedException.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ContentNotRecognizedException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an ContentNotRecognizedException.
   *
   * @param message the exception message.
   */
  public ContentNotRecognizedException( final String message ) {
    super( message );
  }
}
