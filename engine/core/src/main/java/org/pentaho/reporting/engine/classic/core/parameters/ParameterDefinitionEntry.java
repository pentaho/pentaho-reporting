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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Contains the definition of a single parameter, along with means to validate the parameter on the server side and
 * means to attach arbitrary data to the parameter.
 *
 * @author Thomas Morgner
 */
public interface ParameterDefinitionEntry extends Serializable, Cloneable {
  /**
   * The internal parameter name. This will be the name of the data-row field by which the parameter's value can be
   * accessed.
   *
   * @return the parameter name.
   */
  public String getName();

  /**
   * Returns the parameter label. This is optional, but provides a sensible default for auto-generated parameter pages.
   *
   * @param domain           the parameter domain (namespace)
   * @param name             the name of the parameter attribute
   * @param parameterContext the context from where to aquire the locale for the label.
   * @return the label.
   */
  public String getParameterAttribute( final String domain, final String name,
                                       final ParameterContext parameterContext );

  public String[] getParameterAttributeNamespaces();

  public String[] getParameterAttributeNames( final String domainName );

  /**
   * Provides a hint to the parameter validator, whether this value needs to have a selected value.
   *
   * @return true, if the parameter must have a valid value, false otherwise.
   */
  public boolean isMandatory();

  /**
   * Returns the parameter value type. This is used to perform a simple validation on the incoming untrusted data and
   * converts any incoming text value into a sensible default.
   *
   * @return the expected value type.
   */
  public Class getValueType();

  public Object getDefaultValue( final ParameterContext context ) throws ReportDataFactoryException;

  Object clone() throws CloneNotSupportedException;

  default String getTranslatedParameterAttribute( final String namespace,
                                                  final String name,
                                                  final ParameterContext context ) {
    final String formulaName = name + "-formula";
    final String formula = getParameterAttribute( namespace, formulaName, context );
    if ( StringUtils.isEmpty( formula, true ) ) {
      return getParameterAttribute( namespace, name, context );
    } else {
      try {
        ParameterExpressionRuntime runtime = new ParameterExpressionRuntime( context,
          context.getParameterData() );
        final FormulaExpression fe = new FormulaExpression();
        fe.setFormula( formula );
        fe.setRuntime( runtime );
        final Object res = fe.getValue();

        return String.valueOf( res );
      } catch ( ReportProcessingException e ) {
        return getParameterAttribute( namespace, name, context );
      }
    }
  }
}
