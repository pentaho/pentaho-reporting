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
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.ui.datasources.kettle.embedded.KettleParameterInfo;

public class FormulaParameterDialog extends CommonDialog
{
  public static class EditResult
  {
    private FormulaArgument[] argumentNames;
    private FormulaParameter[] parameterMappings;

    public EditResult(final FormulaArgument[] argumentNames,
                      final FormulaParameter[] parameterMappings)
    {
      ArgumentNullException.validate("argumentNames", argumentNames);
      ArgumentNullException.validate("parameterMappings", parameterMappings);

      this.argumentNames = argumentNames;
      this.parameterMappings = parameterMappings;
    }

    public FormulaArgument[] getArgumentNames()
    {
      return argumentNames.clone();
    }

    public FormulaParameter[] getParameterMappings()
    {
      return parameterMappings.clone();
    }
  }

  private FormulaParameterEditor editor;
  private DesignTimeContext context;

  public FormulaParameterDialog(final Dialog owner,
                                final DesignTimeContext context) throws HeadlessException
  {
    super(owner);
    this.context = context;
    init();
  }

  protected String getDialogId()
  {
    return getClass().getName();
  }

  protected void init()
  {
    editor = new FormulaParameterEditor();
    editor.setFormulaContext(this.context.getDataFactoryContext().getFormulaContext());
    super.init();
  }

  public EditResult performEdit(final FormulaArgument[] argumentNames,
                                final FormulaParameter[] parameterMappings,
                                final FieldDefinition[] reportFields,
                                final KettleParameterInfo[] transformationParameters)
  {
    editor.setFields(reportFields);
    editor.setFormulaParameter(build(argumentNames, parameterMappings, transformationParameters));

    if (performEdit() == false)
    {
      return null;
    }


    return new EditResult(extractArguments(), extractParameter());
  }

  private FormulaParameter[] extractParameter()
  {
    FormulaParameterEntity[] formulaParameter = editor.getFormulaParameter();
    List<FormulaParameter> l = new ArrayList<FormulaParameter>();
    for (FormulaParameterEntity entity : formulaParameter)
    {
      if (entity.getType() != FormulaParameterEntity.Type.PARAMETER)
      {
        continue;
      }


      String value = entity.getValue();
      if (StringUtils.isEmpty(value, true) == false)
      {
        l.add(new FormulaParameter(entity.getName(), value));
      }
    }
    return l.toArray(new FormulaParameter[l.size()]);
  }

  private FormulaArgument[] extractArguments()
  {
    FormulaParameterEntity[] formulaParameter = editor.getFormulaParameter();
    List<FormulaArgument> l = new ArrayList<FormulaArgument>();
    for (FormulaParameterEntity entity : formulaParameter)
    {
      if (entity.getType() != FormulaParameterEntity.Type.ARGUMENT)
      {
        continue;
      }

      String value = entity.getValue();
      l.add(new FormulaArgument(value));
    }
    return l.toArray(new FormulaArgument[l.size()]);
  }

  private FormulaParameterEntity[] build(final FormulaArgument[] argumentNames,
                                         final FormulaParameter[] parameterMappings,
                                         final KettleParameterInfo[] transformationParameters)
  {
    ArrayList<FormulaParameterEntity> l = new ArrayList<FormulaParameterEntity>();
    for (FormulaArgument argumentName : argumentNames)
    {
      l.add(FormulaParameterEntity.createArgument(argumentName.getFormula()));
    }
    HashSet<String> usedNames = new HashSet<String>();
    for (FormulaParameter parameterMapping : parameterMappings)
    {
      l.add(new FormulaParameterEntity(FormulaParameterEntity.Type.PARAMETER,
          parameterMapping.getName(), parameterMapping.getFormula()));
      usedNames.add(parameterMapping.getName());
    }
    for (KettleParameterInfo transformationParameter : transformationParameters)
    {
      if (usedNames.contains(transformationParameter.getName()))
      {
        continue;
      }
      l.add(new FormulaParameterEntity(FormulaParameterEntity.Type.PARAMETER,
          transformationParameter.getName(), null));
    }
    return l.toArray(new FormulaParameterEntity[l.size()]);
  }

  protected Component createContentPane()
  {
    return editor;
  }
}
