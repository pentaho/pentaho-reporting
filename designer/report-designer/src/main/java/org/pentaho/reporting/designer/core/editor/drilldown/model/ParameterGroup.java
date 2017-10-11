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

import java.util.LinkedHashMap;

/**
 * Todo: Document me!
 * <p/>
 * Date: 22.07.2010 Time: 13:51:53
 *
 * @author Thomas Morgner.
 */
public class ParameterGroup {
  private String name;
  private String label;
  private LinkedHashMap<String, Parameter> parameters;

  public ParameterGroup( final String name, final String parameterGroupLabel ) {
    this.name = name;
    this.label = parameterGroupLabel;
    this.parameters = new LinkedHashMap<String, Parameter>();
  }

  public String getLabel() {
    return label;
  }

  public String getName() {
    return name;
  }

  public void addParameter( final Parameter parameter ) {
    parameters.put( parameter.getName(), parameter );
  }

  public Parameter getParameter( final String parameter ) {
    return parameters.get( parameter );
  }

  public Parameter[] getParameters() {
    return parameters.values().toArray( new Parameter[ parameters.size() ] );
  }
}
