/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;

public class MailDefinitionTest {

  @Test
  public void testClone() throws CloneNotSupportedException {
    MasterReport bodyReport = mock( MasterReport.class );
    MasterReport clonedReport = mock( MasterReport.class );
    ReportProcessTaskMetaData exportTask = mock( ReportProcessTaskMetaData.class );
    doReturn( "attach_type" ).when( exportTask ).getName();
    ReportProcessTaskRegistry.getInstance().registerExportType( exportTask );
    doReturn( clonedReport ).when( bodyReport ).clone();

    MailDefinition source = new MailDefinition( "body-type", bodyReport );
    source.addAttachmentReport( "attach_type", bodyReport );

    Object cloned = source.clone();
    assertThat( cloned, is( instanceOf( MailDefinition.class ) ) );
    MailDefinition result = (MailDefinition) cloned;
    assertThat( result, is( not( sameInstance( source ) ) ) );
    assertThat( result.getBodyReport(), is( notNullValue() ) );
    assertThat( result.getBodyReport(), is( not( sameInstance( source.getBodyReport() ) ) ) );
    assertThat( result.getBodyType(), is( equalTo( source.getBodyType() ) ) );
    assertThat( result.getAttachmentCount(), is( equalTo( source.getAttachmentCount() ) ) );
    assertThat( result.getAttachmentReport( 0 ), is( not( sameInstance( source.getAttachmentReport( 0 ) ) ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testAddIncorectHeader() {
    MailDefinition source = new MailDefinition();
    source.addHeader( null );
  }

  @Test
  public void testAddHeader() {
    MailDefinition source = new MailDefinition();
    MailHeader header = new StaticHeader( "name", "val" );
    source.addHeader( header );
    assertThat( source.getHeaderCount(), is( equalTo( 1 ) ) );
    assertThat( source.getHeader( 0 ), is( equalTo( header ) ) );
  }

  @Test
  public void testAddStaticHeader() {
    MailDefinition source = new MailDefinition();
    source.addStaticHeader( "name", "val" );
    assertThat( source.getHeaderCount(), is( equalTo( 1 ) ) );
    assertThat( source.getHeader( 0 ), is( instanceOf( StaticHeader.class ) ) );
  }

  @Test
  public void testAddFormulaHeader() {
    MailDefinition source = new MailDefinition();
    source.addFormulaHeader( "name", "val" );
    assertThat( source.getHeaderCount(), is( equalTo( 1 ) ) );
    assertThat( source.getHeader( 0 ), is( instanceOf( FormulaHeader.class ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testAddAttachmentReportWithoutType() {
    MailDefinition source = new MailDefinition();
    source.addAttachmentReport( null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testAddAttachmentReportWithoutReport() {
    MailDefinition source = new MailDefinition();
    source.addAttachmentReport( "type", null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttachmentReportWithIncorrectType() {
    MailDefinition source = new MailDefinition();
    source.addAttachmentReport( "type", mock( MasterReport.class ) );
  }

  @Test
  public void testAddAttachmentReport() {
    MailDefinition source = new MailDefinition();
    MasterReport bodyReport = mock( MasterReport.class );
    ReportProcessTaskMetaData exportTask = mock( ReportProcessTaskMetaData.class );
    doReturn( "attach_type" ).when( exportTask ).getName();
    ReportProcessTaskRegistry.getInstance().registerExportType( exportTask );
    source.addAttachmentReport( "attach_type", bodyReport );

    assertThat( source.getAttachmentCount(), is( equalTo( 1 ) ) );
    assertThat( source.getAttachmentReport( 0 ), is( equalTo( bodyReport ) ) );
    assertThat( source.getAttachmentType( 0 ), is( equalTo( "attach_type" ) ) );

    source.removeAttachment( 0 );

    assertThat( source.getAttachmentCount(), is( equalTo( 0 ) ) );
  }
}
