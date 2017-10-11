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

package org.pentaho.reporting.designer.core.editor.drilldown.model;

/**
 * Todo: Document me!
 * <p/>
 * Date: 22.07.2010 Time: 18:01:47
 *
 * @author Thomas Morgner.
 */
public class ParameterSelection {
  private String label;
  private String type;
  private boolean selected;
  private String value;

  public ParameterSelection( final String type, final String value, final boolean selected, final String label ) {
    this.type = type;
    this.value = value;
    this.selected = selected;
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public String getType() {
    return type;
  }

  public boolean isSelected() {
    return selected;
  }

  public String getValue() {
    return value;
  }
}
