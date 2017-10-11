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

package org.pentaho.reporting.libraries.repository;

/**
 * A name generator is a general service to generate unique names within an content location.
 *
 * @author Thomas Morgner
 */
public interface NameGenerator {
  /**
   * Generates a new name for the location. The name-generator may use both the name-hint and mimetype to compute the
   * new name.
   *
   * @param nameHint the name hint, usually a identifier for the new filename (can be null).
   * @param mimeType the mime type of the new filename. Usually used to compute a suitable file-suffix.
   * @return the generated name, never null.
   * @throws ContentIOException if the name could not be generated for any reason.
   */
  public String generateName( String nameHint, String mimeType )
    throws ContentIOException;
}
