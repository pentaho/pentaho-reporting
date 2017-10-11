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
 * Creation-Date: 25.07.2006, 18:25:13
 *
 * @author Thomas Morgner
 */
public class DefaultLanguageClassifier implements LanguageClassifier {

  private int lastScript;

  public DefaultLanguageClassifier() {
  }

  public int getScript( final int codepoint ) {
    return 0;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
