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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.mondrian;

import java.awt.Dialog;
import java.awt.Frame;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.SimpleBandedMDXDataFactory;

/**
 * @author Michael D'Amour
 */
public class SimpleBandedMdxDataSourceEditor extends SimpleMondrianDataSourceEditor
{
  public SimpleBandedMdxDataSourceEditor(final DesignTimeContext context)
  {
    super(context);
  }

  public SimpleBandedMdxDataSourceEditor(final DesignTimeContext context, final Dialog owner)
  {
    super(context, owner);
  }

  public SimpleBandedMdxDataSourceEditor(final DesignTimeContext context, final Frame owner)
  {
    super(context, owner);
  }

  protected void init(final DesignTimeContext context)
  {
    super.init(context);
    setTitle(Messages.getString("SimpleBandedMdxDataSourceEditor.Title"));
  }

  protected String getDialogId()
  {
    return "MondrianDataSourceEditor.SimpleBanded";
  }

  protected AbstractMDXDataFactory createDataFactory()
  {
    final SimpleBandedMDXDataFactory returnDataFactory = new SimpleBandedMDXDataFactory();
    configureConnection(returnDataFactory);
    return returnDataFactory;
  }

}