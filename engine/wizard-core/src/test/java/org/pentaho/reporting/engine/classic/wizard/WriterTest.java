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

package org.pentaho.reporting.engine.classic.wizard;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;

public class WriterTest extends TestCase {
  public WriterTest() {
  }

  public WriterTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmptyWrite() throws Exception {
    final DefaultWizardSpecification wizardSpecification = new DefaultWizardSpecification();
    final MasterReport report = new MasterReport();
    report.setAttribute( AttributeNames.Wizard.NAMESPACE, "wizard-spec", wizardSpecification );

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( report, byteArrayOutputStream );

    final byte[] data = byteArrayOutputStream.toByteArray();
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( data, MasterReport.class );
    final MasterReport parsedReport = (MasterReport) directly.getResource();
    assertNotNull( parsedReport );
    final WizardSpecification specification =
      WizardProcessorUtil.loadWizardSpecification( report, report.getResourceManager() );
    assertNotNull( specification );
  }

  public void testNonLoad() throws ReportProcessingException {
    final MasterReport report = new MasterReport();
    assertNull( WizardProcessorUtil.loadWizardSpecification( report, report.getResourceManager() ) );
  }

  public void testFullRelationalWrite() throws Exception {
    final GroupDefinition[] groupDefs = new GroupDefinition[ 3 ];
    groupDefs[ 0 ] = new DefaultGroupDefinition( GroupType.RELATIONAL, "group-field1" );
    groupDefs[ 1 ] = new DefaultGroupDefinition( GroupType.RELATIONAL, "group-field2" );
    groupDefs[ 2 ] = new DefaultGroupDefinition( GroupType.RELATIONAL, "group-field3" );

    final DetailFieldDefinition[] detailFields = new DetailFieldDefinition[ 3 ];
    detailFields[ 0 ] = new DefaultDetailFieldDefinition( "detail-field1" );
    detailFields[ 1 ] = new DefaultDetailFieldDefinition( "detail-field2" );
    detailFields[ 2 ] = new DefaultDetailFieldDefinition( "detail-field3" );

    final DefaultWizardSpecification wizardSpecification = new DefaultWizardSpecification();
    wizardSpecification.setGroupDefinitions( groupDefs );
    wizardSpecification.setDetailFieldDefinitions( detailFields );

    final MasterReport report = new MasterReport();
    report.setAttribute( AttributeNames.Wizard.NAMESPACE, "wizard-spec", wizardSpecification );

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( report, byteArrayOutputStream );

    final byte[] data = byteArrayOutputStream.toByteArray();
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( data, MasterReport.class );
    final MasterReport parsedReport = (MasterReport) directly.getResource();
    assertNotNull( parsedReport );

    final WizardSpecification specification =
      WizardProcessorUtil.loadWizardSpecification( report, report.getResourceManager() );
    assertNotNull( specification );

    final GroupDefinition[] resultGroups = specification.getGroupDefinitions();
    assertEquals( 3, resultGroups.length );
    assertEquals( "group-field1", resultGroups[ 0 ].getField() );
    assertEquals( "group-field2", resultGroups[ 1 ].getField() );
    assertEquals( "group-field3", resultGroups[ 2 ].getField() );
    assertEquals( GroupType.RELATIONAL, resultGroups[ 0 ].getGroupType() );
    assertEquals( GroupType.RELATIONAL, resultGroups[ 1 ].getGroupType() );
    assertEquals( GroupType.RELATIONAL, resultGroups[ 2 ].getGroupType() );

    final DetailFieldDefinition[] resultDetails = specification.getDetailFieldDefinitions();
    assertEquals( 3, resultDetails.length );
    assertEquals( "detail-field1", resultDetails[ 0 ].getField() );
    assertEquals( "detail-field2", resultDetails[ 1 ].getField() );
    assertEquals( "detail-field3", resultDetails[ 2 ].getField() );

  }


  public void testFullCrosstabWrite() throws Exception {
    final GroupDefinition[] groupDefs = new GroupDefinition[ 3 ];
    groupDefs[ 0 ] = new DefaultGroupDefinition( GroupType.RELATIONAL, "group-field1" );
    groupDefs[ 1 ] = new DefaultGroupDefinition( GroupType.CT_ROW, "group-field2" );
    groupDefs[ 2 ] = new DefaultGroupDefinition( GroupType.CT_COLUMN, "group-field3" );

    final DetailFieldDefinition[] detailFields = new DetailFieldDefinition[ 3 ];
    detailFields[ 0 ] = new DefaultDetailFieldDefinition( "detail-field1" );
    detailFields[ 1 ] = new DefaultDetailFieldDefinition( "detail-field2" );
    detailFields[ 2 ] = new DefaultDetailFieldDefinition( "detail-field3" );

    final DefaultWizardSpecification wizardSpecification = new DefaultWizardSpecification();
    wizardSpecification.setGroupDefinitions( groupDefs );
    wizardSpecification.setDetailFieldDefinitions( detailFields );

    final MasterReport report = new MasterReport();
    report.setAttribute( AttributeNames.Wizard.NAMESPACE, "wizard-spec", wizardSpecification );

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( report, byteArrayOutputStream );

    final byte[] data = byteArrayOutputStream.toByteArray();
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( data, MasterReport.class );
    final MasterReport parsedReport = (MasterReport) directly.getResource();
    assertNotNull( parsedReport );

    final WizardSpecification specification =
      WizardProcessorUtil.loadWizardSpecification( report, report.getResourceManager() );
    assertNotNull( specification );

    final GroupDefinition[] resultGroups = specification.getGroupDefinitions();
    assertEquals( 3, resultGroups.length );
    assertEquals( "group-field1", resultGroups[ 0 ].getField() );
    assertEquals( "group-field2", resultGroups[ 1 ].getField() );
    assertEquals( "group-field3", resultGroups[ 2 ].getField() );
    assertEquals( GroupType.RELATIONAL, resultGroups[ 0 ].getGroupType() );
    assertEquals( GroupType.CT_ROW, resultGroups[ 1 ].getGroupType() );
    assertEquals( GroupType.CT_COLUMN, resultGroups[ 2 ].getGroupType() );

    final DetailFieldDefinition[] resultDetails = specification.getDetailFieldDefinitions();
    assertEquals( 3, resultDetails.length );
    assertEquals( "detail-field1", resultDetails[ 0 ].getField() );
    assertEquals( "detail-field2", resultDetails[ 1 ].getField() );
    assertEquals( "detail-field3", resultDetails[ 2 ].getField() );

  }
}
