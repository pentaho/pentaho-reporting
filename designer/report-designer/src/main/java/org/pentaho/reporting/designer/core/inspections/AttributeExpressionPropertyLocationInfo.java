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

package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.engine.classic.core.ReportElement;

public class AttributeExpressionPropertyLocationInfo extends LocationInfo {
  private String attributeName;
  private String attributeNamespace;
  private String expressionProperty;

  public AttributeExpressionPropertyLocationInfo( final ReportElement reportElement,
                                                  final String attributeNamespace,
                                                  final String attributeName,
                                                  final String expressionProperty ) {
    super( reportElement );
    if ( attributeName == null ) {
      throw new NullPointerException();
    }
    if ( attributeNamespace == null ) {
      throw new NullPointerException();
    }
    if ( expressionProperty == null ) {
      throw new NullPointerException();
    }

    this.attributeNamespace = attributeNamespace;
    this.expressionProperty = expressionProperty;
    this.attributeName = attributeName;
  }

  public String getAttributeNamespace() {
    return attributeNamespace;
  }

  public String getExpressionProperty() {
    return expressionProperty;
  }

  public String getAttributeName() {
    return attributeName;
  }

}
