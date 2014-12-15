/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.builder;

import java.beans.PropertyEditor;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.SharedPropertyDescriptorProxy;

public class ExpressionPropertyMetaDataBuilder extends MetaDataBuilder<ExpressionPropertyMetaDataBuilder>
{
  private boolean mandatory;
  private boolean computed;
  private String valueRole;
  private Class<? extends PropertyEditor> editor;
  private ExpressionPropertyCore core;
  private SharedPropertyDescriptorProxy descriptor;
  private Class<? extends Expression> expression;

  public ExpressionPropertyMetaDataBuilder()
  {
    this.core = new DefaultExpressionPropertyCore();
  }

  protected ExpressionPropertyMetaDataBuilder self()
  {
    return this;
  }

  public ExpressionPropertyMetaDataBuilder descriptor(SharedPropertyDescriptorProxy descriptor)
  {
    this.descriptor = descriptor;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder descriptorFromParent(Class<? extends Expression> expression)
  {
    this.expression = expression;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder mandatory(final boolean mandatory)
  {
    this.mandatory = mandatory;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder computed(final boolean computed)
  {
    this.computed = computed;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder valueRole(final String valueRole)
  {
    this.valueRole = valueRole;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder editor(final Class<? extends PropertyEditor> propertyEditorClass)
  {
    this.editor = propertyEditorClass;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder core(final ExpressionPropertyCore expressionPropertyCore)
  {
    this.core = expressionPropertyCore;
    return self();
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public boolean isComputed()
  {
    return computed;
  }

  public String getValueRole()
  {
    return valueRole;
  }

  public Class<? extends PropertyEditor> getEditor()
  {
    return editor;
  }

  public ExpressionPropertyCore getCore()
  {
    return core;
  }

  public SharedPropertyDescriptorProxy getDescriptor()
  {
    if (descriptor != null)
    {
      return descriptor;
    }
    if (expression != null && getName() != null) {
      return new SharedPropertyDescriptorProxy(expression, getName());
    }
    return null;
  }
}
