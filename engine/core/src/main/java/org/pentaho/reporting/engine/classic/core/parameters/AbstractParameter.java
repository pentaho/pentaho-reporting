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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * A simple parameter that represents a single value. This value is set by the user.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractParameter implements ParameterDefinitionEntry {
  private static final Log logger = LogFactory.getLog( AbstractParameter.class );

  private String name;
  private ReportAttributeMap attributeMap;
  private boolean mandatory;
  private Class valueType;
  private Object defaultValue;

  protected AbstractParameter( final String name ) {
    this( name, String.class );
  }

  protected AbstractParameter( final String name, final Class valueType ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( valueType == null ) {
      throw new NullPointerException();
    }

    this.name = name;
    this.valueType = valueType;
    this.attributeMap = new ReportAttributeMap();
    setRole( ParameterAttributeNames.Core.ROLE_USER_PARAMETER );
  }

  /**
   * The internal parameter name. This will be the name of the data-row field by which the parameter's value can be
   * accessed.
   *
   * @return the parameter name.
   */
  public String getName() {
    return name;
  }

  public void setParameterAttribute( final String domain, final String name, final String value ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    attributeMap.setAttribute( domain, name, value );
  }

  public String getParameterAttribute( final String domain, final String name ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    return (String) attributeMap.getAttribute( domain, name );
  }

  public String getParameterAttribute( final String domain, final String name, final ParameterContext parameterContext ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    return (String) attributeMap.getAttribute( domain, name );
  }

  public String[] getParameterAttributeNamespaces() {
    return attributeMap.getNameSpaces();
  }

  public String[] getParameterAttributeNames( final String domain ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    return attributeMap.getNames( domain );
  }

  /**
   * Provides a hint to the parameter validator, whether this value needs to have a selected value.
   *
   * @return true, if the parameter must have a valid value, false otherwise.
   */
  public boolean isMandatory() {
    return mandatory;
  }

  public void setMandatory( final boolean mandatory ) {
    this.mandatory = mandatory;
  }

  /**
   * Returns the parameter value type. This is used to perform a simple validation on the incoming untrusted data and
   * converts any incoming text value into a sensible default.
   *
   * @return
   */
  public Class getValueType() {
    return valueType;
  }

  public void setValueType( final Class valueType ) {
    if ( valueType == null ) {
      throw new NullPointerException();
    }
    this.valueType = valueType;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  public Object getDefaultValue( final ParameterContext context ) throws ReportDataFactoryException {
    final String formula =
        (String) attributeMap.getAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DEFAULT_VALUE_FORMULA );
    if ( StringUtils.isEmpty( formula, false ) == false ) {
      // evaluate
      try {
        final Object defaultValue =
            FormulaParameterEvaluator.computeValue(
                new ParameterExpressionRuntime( context, context.getParameterData() ), formula, null, this,
                this.defaultValue );
        if ( defaultValue != null ) {
          return defaultValue;
        }
      } catch ( ReportProcessingException e ) {
        logger.debug( "Unable to compute default value for parameter '" + name + '"', e );
      }
    }
    return defaultValue;
  }

  public void setDefaultValue( final Object defaultValue ) {
    this.defaultValue = defaultValue;
  }

  public Object clone() throws CloneNotSupportedException {
    final AbstractParameter o = (AbstractParameter) super.clone();
    o.attributeMap = (ReportAttributeMap) attributeMap.clone();
    return o;
  }

  public boolean isHidden() {
    return "true".equals( getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.HIDDEN ) );
  }

  public void setHidden( final boolean hidden ) {
    setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.HIDDEN, String
        .valueOf( hidden ) );
  }

  public boolean isDeprecated() {
    return "true".equals( getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.DEPRECATED ) );
  }

  public void setDeprecated( final boolean deprecated ) {
    setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DEPRECATED, String
        .valueOf( deprecated ) );
  }

  public String getRole() {
    return getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.ROLE );
  }

  public void setRole( final String role ) {
    setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.ROLE, role );
  }
}
