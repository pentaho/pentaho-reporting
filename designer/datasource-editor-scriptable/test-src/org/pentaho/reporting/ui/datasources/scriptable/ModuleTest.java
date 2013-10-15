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

package org.pentaho.reporting.ui.datasources.scriptable;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;

public class ModuleTest extends TestCase
{
  public ModuleTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testModuleExists()
  {
    assertTrue(ClassicEngineBoot.getInstance().getPackageManager().isModuleAvailable(ScriptableDataSourceModule.class.getName()));
  }

  public void testEditorRegistered()
  {
    DataSourcePlugin editor =
        DataFactoryRegistry.getInstance().getMetaData(ScriptableDataFactory.class.getName()).createEditor();
    assertNotNull(editor);

    assertTrue(editor.canHandle(new ScriptableDataFactory()));
  }

}
