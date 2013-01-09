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

package org.pentaho.reporting.engine.classic.extensions.swt.demo;

import java.net.URL;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.pentaho.reporting.engine.classic.demo.ClassicEngineDemoBoot;
import org.pentaho.reporting.engine.classic.extensions.swt.demo.groups.GroupsDemo;
import org.pentaho.reporting.engine.classic.extensions.swt.demo.util.CompoundDemoFrame;
import org.pentaho.reporting.engine.classic.extensions.swt.demo.util.DefaultDemoSelector;
import org.pentaho.reporting.engine.classic.extensions.swt.demo.util.DemoSelector;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class DemoFrontend extends CompoundDemoFrame
{
  private Composite infoPane;

  public DemoFrontend(final DemoSelector demoSelector)
  {
    super(demoSelector);
    setIgnoreEmbeddedConfig(true);
    final ModifiableConfiguration editableConfig = ClassicEngineDemoBoot
        .getInstance().getEditableConfig();
    editableConfig.setConfigProperty(EMBEDDED_KEY, "true");
}

public static DemoSelector createDemoInfo()
{
  final DefaultDemoSelector rootSelector = new DefaultDemoSelector(
      "All JFreeReport Demos");

  // the most important demos first: the ones that get you started
  rootSelector.addDemo(new HelloWorld());
  rootSelector.addDemo(new GroupsDemo());

  return rootSelector;
}

protected Composite getNoHandlerInfoPane(final Composite parent)
{
  if (infoPane == null)
  {
    final URL url = ObjectUtilities.getResource(
        "org/pentaho/reporting/engine/classic/demo/demo-introduction.html",
          CompoundDemoFrame.class);

      infoPane = createDescriptionTextPane(parent, url);
    }
    return infoPane;
  }

  public static void main(final String[] args)
  {
    ClassicEngineDemoBoot.getInstance().start();

    final DemoFrontend frontend = new DemoFrontend(createDemoInfo());
    frontend.setBlockOnOpen(true);
    frontend.open();
    Display.getCurrent().dispose();
  }
}
