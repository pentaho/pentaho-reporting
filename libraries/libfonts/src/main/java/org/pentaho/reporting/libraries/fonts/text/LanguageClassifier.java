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

package org.pentaho.reporting.libraries.fonts.text;

/**
 * unicode standard).
 * <p/>
 * The language code returned by this class is an integer, which maps to one of the ISO 15924 numerical language codes.
 * If there is an Unicode script tag, for which there exists no corresponding ISO code, then negative numbers are
 * assigned in descending order.
 *
 * @author Thomas Morgner
 */
public interface LanguageClassifier extends ClassificationProducer {
  public int getScript( int codepoint );

  // Todo: Derive the language from the characters. (See "scripts.txt" in the

}
