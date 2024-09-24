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

package org.pentaho.reporting.designer.core;

import org.pentaho.ui.xul.impl.XulEventHandler;

import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface ReportDesignerUiPlugin {
  /**
   * Returny any extra handlers. Never return null.
   *
   * @return
   */
  public Map<String, String> getXulAdditionalHandlers();

  /**
   * Returns the overlay source file. Can be null, if no overlay is needed.
   *
   * @return
   */
  public String[] getOverlaySources();

  public XulEventHandler[] createEventHandlers();
}
