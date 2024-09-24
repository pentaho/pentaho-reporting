/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.richtext;

import org.pentaho.reporting.engine.classic.core.ReportElement;

/**
 * A worker that converts raw-objects into rich-text objects. The worker expects either a byte-array, a char-array, a
 * string or a native Document and returns the converted value (or the value unchanged if its not a rich-text object).
 *
 * @author Thomas Morgner.
 */
public interface RichTextConverter {
  public boolean isRecognizedType( final String mimeType );

  public Object convert( final ReportElement source, final Object value );
}
