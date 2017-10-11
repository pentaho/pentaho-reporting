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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010 Time: 17:22:26
 *
 * @author Thomas Morgner.
 */
public class ParameterDocument {
  private boolean promptNeeded;
  private boolean paginate;
  private String layout;
  private Boolean autoSubmit;
  private boolean showParameterUi;
  private LinkedHashMap<String, ParameterGroup> parameters;
  private boolean autoSubmitUI;
  private boolean subscribe;

  public ParameterDocument() {
    parameters = new LinkedHashMap<String, ParameterGroup>();
    layout = "vertical"; // NON-NLS
  }

  public boolean isShowParameterUi() {
    return showParameterUi;
  }

  public void setShowParameterUi( final boolean showParameterUi ) {
    this.showParameterUi = showParameterUi;
  }

  public boolean isPromptNeeded() {
    return promptNeeded;
  }

  public void setPromptNeeded( final boolean promptNeeded ) {
    this.promptNeeded = promptNeeded;
  }

  public boolean isPaginate() {
    return paginate;
  }

  public void setPaginate( final boolean paginate ) {
    this.paginate = paginate;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout( final String layout ) {
    this.layout = layout;
  }

  public ParameterGroup getParameterGroup( final String name ) {
    return parameters.get( name );
  }

  public void addParameterGroup( final ParameterGroup parameterGroup ) {
    if ( parameterGroup == null ) {
      throw new NullPointerException();
    }
    parameters.put( parameterGroup.getName(), parameterGroup );
  }

  public Parameter getParameter( final String name ) {
    final Collection<ParameterGroup> parameterGroupCollection = parameters.values();
    for ( final ParameterGroup parameterGroup : parameterGroupCollection ) {
      final Parameter parameter = parameterGroup.getParameter( name );
      if ( parameter != null ) {
        return parameter;
      }
    }
    return null;
  }

  public boolean isEmpty() {
    return parameters.isEmpty();
  }

  public boolean isPaginationControlNeeded() {
    if ( promptNeeded == false && paginate ) //$NON-NLS-1$ //$NON-NLS-2$
    {
      return true;
    }
    return false;
  }

  public Boolean getAutoSubmit() {
    return autoSubmit;
  }

  public void setAutoSubmit( final Boolean autoSubmit ) {
    this.autoSubmit = autoSubmit;
  }

  public boolean isAutoSubmitUI() {
    return autoSubmitUI;
  }

  public void setAutoSubmitUI( final boolean autoSubmitUI ) {
    this.autoSubmitUI = autoSubmitUI;
  }

  public boolean isSubscribe() {
    return subscribe;
  }

  public void setSubscribe( final boolean subscribe ) {
    this.subscribe = subscribe;
  }

  public ParameterGroup[] getParameterGroups() {
    return this.parameters.values().toArray( new ParameterGroup[ parameters.size() ] );
  }

  public Parameter[] getParameter() {
    final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
    final ParameterGroup[] groups = getParameterGroups();
    for ( int i = 0; i < groups.length; i++ ) {
      final ParameterGroup group = groups[ i ];
      final Parameter[] parameters1 = group.getParameters();
      for ( int j = 0; j < parameters1.length; j++ ) {
        parameters.add( parameters1[ j ] );
      }
    }

    return parameters.toArray( new Parameter[ parameters.size() ] );
  }
}
