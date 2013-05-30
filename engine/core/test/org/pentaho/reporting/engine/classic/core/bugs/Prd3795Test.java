/*
 *
 *  * This program is free software; you can redistribute it and/or modify it under the
 *  * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  * Foundation.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License along with this
 *  * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  * or from the Free Software Foundation, Inc.,
 *  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  * See the GNU Lesser General Public License for more details.
 *  *
 *  * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 *
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.StaticListParameter;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd3795Test extends TestCase
{
  public Prd3795Test()
  {
  }

  public Prd3795Test(final String name)
  {
    super(name);
  }

  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
   // File("bin/test-tmp").mkdirs();
  }

  /**
   * tests save and reopen of a report containing a multi-value parameter
   * without a query (a <code>StaticListParameter</code>.
   * Verifies that <code>DataDefinitionFileWriter</code> and
   * <code>ListParameterReadHandler</code> correctly handle such parameters.
   */
  public void testSaveAndLoadOfMultivalueParameterWithoutQuery() throws Exception
  {
    final Class valueType = Array.newInstance(String.class,0 ).getClass();
    final StaticListParameter listParameter = new StaticListParameter(
        "name", true, true, valueType);
    final String[] defaultValue = {"item 1", "item 2"};
    listParameter.setDefaultValue(defaultValue);
    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    parameterDefinition.addParameterDefinition(listParameter);

    final MasterReport report = new MasterReport();
    report.setParameterDefinition(parameterDefinition);

    final File testReport = File.createTempFile("prd-3795-", ".prpt");
    saveReport(report, testReport);

    final MasterReport reopenedReport = GoldTestBase.parseReport(testReport);
    final StaticListParameter param = (StaticListParameter)reopenedReport.getParameterDefinition().getParameterDefinition(0);

    assertEquals("Parameter type should be String array",
        valueType, param.getValueType());
    assertEquals("name", param.getName());
    assertTrue("Default values of reloaded report do not match",
        Arrays.equals(defaultValue, (String[])param.getDefaultValue()));
    testReport.delete();
  }



  /**
   * This method does what the report designer does on save.
   */
  private void saveReport(final MasterReport report, final File file)
      throws Exception
  {
    BundleWriter.writeReportToZipFile(report, file);
    final ResourceManager resourceManager = report.getResourceManager();
    final Resource bundleResource = resourceManager.createDirectly(file, DocumentBundle.class);
    final DocumentBundle bundle = (DocumentBundle) bundleResource.getResource();
    final ResourceKey bundleKey = bundle.getBundleKey();

    final MemoryDocumentBundle mem = new MemoryDocumentBundle();
    BundleUtilities.copyStickyInto(mem, bundle);
    BundleUtilities.copyMetaData(mem, bundle);
    report.setBundle(mem);
    report.setContentBase(mem.getBundleMainKey());
    report.setDefinitionSource(bundleKey);
  }
}
