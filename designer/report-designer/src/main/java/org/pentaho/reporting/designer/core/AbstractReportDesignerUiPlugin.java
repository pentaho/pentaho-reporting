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

import java.util.Collections;
import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class AbstractReportDesignerUiPlugin implements ReportDesignerUiPlugin {
  @SuppressWarnings( { "StaticCollection" } )
  private static final Map<String, String> EMPTY = Collections.EMPTY_MAP;
  private static final String[] EMPTY_SOURCES = new String[ 0 ];
  private static final XulEventHandler[] EMPTY_EVENTHANDLERS = new XulEventHandler[ 0 ];

  protected AbstractReportDesignerUiPlugin() {
  }

  public Map<String, String> getXulAdditionalHandlers() {
    //noinspection ReturnOfCollectionOrArrayField
    return EMPTY;
  }

  public String[] getOverlaySources() {
    return EMPTY_SOURCES;
  }

  public XulEventHandler[] createEventHandlers() {
    return EMPTY_EVENTHANDLERS;
  }
}
