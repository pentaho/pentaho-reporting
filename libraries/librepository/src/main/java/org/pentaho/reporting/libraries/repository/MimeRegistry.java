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
 * The MimeRegistry encodes content-type information and allows to detect or query the file type of a given content
 * item. It also assists in naming files by providing a default suffix for a given mime-type.
 *
 * @author Thomas Morgner
 */
public interface MimeRegistry {
  /**
   * Queries the mime-type for a given content-item. Some repositories store mime-type information along with the
   * content data, while others might resort to heuristics based on the filename or actual data stored in the item.
   *
   * @param item the content item for which Mime-Data should be queried.
   * @return the mime-type never null.
   */
  public String getMimeType( ContentItem item );

  /**
   * Returns the default suffix for files with the given content type.
   *
   * @param mimeType the mime-type for which a suffix is queried.
   * @return the suffix, never null.
   */
  public String getSuffix( String mimeType );
}
