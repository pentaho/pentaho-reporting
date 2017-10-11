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

package org.pentaho.reporting.libraries.formula;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.function.DefaultFunctionRegistry;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.DefaultOperatorFactory;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.DefaultTypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Creation-Date: 31.10.2006, 16:32:32
 *
 * @author Thomas Morgner
 */
public class DefaultFormulaContext implements FormulaContext {
  private DefaultTypeRegistry typeRegistry;
  private DefaultFunctionRegistry functionRegistry;
  private DefaultOperatorFactory operatorFactory;
  private DefaultLocalizationContext localizationContext;
  private Configuration config;
  private HashMap references;

  public DefaultFormulaContext() {
    this( LibFormulaBoot.getInstance().getGlobalConfig() );
  }

  public DefaultFormulaContext( final Configuration config ) {
    this( config, null, null );
  }

  public DefaultFormulaContext( final Configuration config, final Locale locale, final TimeZone timeZone ) {
    if ( config == null ) {
      throw new NullPointerException();
    }

    this.config = config;
    localizationContext = new DefaultLocalizationContext();
    localizationContext.initialize( config, locale, timeZone );
    typeRegistry = new DefaultTypeRegistry();
    typeRegistry.initialize( this );
    functionRegistry = new DefaultFunctionRegistry();
    functionRegistry.initialize( config );
    operatorFactory = new DefaultOperatorFactory();
    operatorFactory.initalize( config );
  }

  public OperatorFactory getOperatorFactory() {
    return operatorFactory;
  }

  public void defineReference( final Object name, final Object value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      if ( references == null ) {
        return;
      }
      references.remove( name );
      return;
    }
    if ( references == null ) {
      references = new HashMap();
    }
    references.put( name, value );
  }

  public Object resolveReference( final Object name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( references == null ) {
      return null;
    }
    return references.get( name );
  }

  public Configuration getConfiguration() {
    return config;
  }

  public FunctionRegistry getFunctionRegistry() {
    return functionRegistry;
  }

  public Type resolveReferenceType( final Object name ) {
    return AnyType.TYPE;
  }

  public TypeRegistry getTypeRegistry() {
    return typeRegistry;
  }

  public LocalizationContext getLocalizationContext() {
    return localizationContext;
  }

  public boolean isReferenceDirty( final Object name ) {
    return true;
  }

  public Date getCurrentDate() {
    return new Date();
  }
}
