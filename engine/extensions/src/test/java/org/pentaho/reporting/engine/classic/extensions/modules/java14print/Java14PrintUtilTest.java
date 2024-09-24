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
 * Copyright (c) 2005 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.java14print;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.gui.print.PrintUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class Java14PrintUtilTest {

  private static final String JOB_NAME = "job_name";
  private static final String REPOT_TITLE = "report_title";


  @Test
  public void testCopyConfiguration() {
    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
    checkCopyConf( null );
    checkCopyConf( attributes );
  }

  private MasterReport mockReport( int orientation ) {
    MasterReport report = mock( MasterReport.class );
    PageDefinition pdef = mock( PageDefinition.class );
    PageFormat format = mock( PageFormat.class );
    Paper paper = mock( Paper.class );
    DefaultConfiguration modConf = mock( DefaultConfiguration.class );
    ResourceManager resourceManager = new ResourceManager();

    doReturn( pdef ).when( report ).getPageDefinition();
    doReturn( modConf ).when( report ).getReportConfiguration();
    doReturn( modConf ).when( report ).getConfiguration();
    doReturn( resourceManager ).when( report ).getResourceManager();

    doReturn( REPOT_TITLE ).when( report ).getTitle();
    doReturn( JOB_NAME ).when( modConf ).getConfigProperty( PrintUtil.PRINTER_JOB_NAME_KEY, REPOT_TITLE );
    doReturn( format ).when( pdef ).getPageFormat( 0 );
    doReturn( paper ).when( format ).getPaper();

    doReturn( 600.0 ).when( paper ).getWidth();
    doReturn( 850.0 ).when( paper ).getHeight();

    doReturn( 10.0 ).when( paper ).getImageableX();
    doReturn( 5.0 ).when( paper ).getImageableY();
    doReturn( 400.0 ).when( paper ).getImageableWidth();
    doReturn( 600.0 ).when( paper ).getImageableHeight();

    doReturn( orientation ).when( format ).getOrientation();
    return report;
  }

  private void checkCopyConf( PrintRequestAttributeSet attributes ) {
    MasterReport report = mockReport( PageFormat.LANDSCAPE );

    PrintRequestAttributeSet result = Java14PrintUtil.copyConfiguration( attributes, report );

    assertThat( result, is( notNullValue() ) );
    assertThat( result.get( Media.class ), is( instanceOf( MediaSizeName.class ) ) );
    assertThat( ( (MediaSizeName) result.get( Media.class ) ).getValue(),
      is( equalTo( MediaSizeName.ISO_A4.getValue() ) ) );
    assertThat( result.get( MediaPrintableArea.class ), is( instanceOf( MediaPrintableArea.class ) ) );
    assertThat( result.get( OrientationRequested.class ), is( instanceOf( OrientationRequested.class ) ) );
    assertThat( (OrientationRequested) result.get( OrientationRequested.class ),
      is( equalTo( OrientationRequested.LANDSCAPE ) ) );
  }

  @Test
  public void testIsValidConfiguration() {
    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
    MasterReport report = mockReport( PageFormat.LANDSCAPE );

    int result = Java14PrintUtil.isValidConfiguration( attributes, report );
    assertThat( result, is( equalTo( Java14PrintUtil.CONFIGURATION_REPAGINATE ) ) );

    attributes.add( new PageRanges( 1 ) );
    result = Java14PrintUtil.isValidConfiguration( attributes, report );
    assertThat( result, is( equalTo( Java14PrintUtil.CONFIGURATION_SHOW_DIALOG ) ) );

    attributes = Java14PrintUtil.copyConfiguration( null, report );
    result = Java14PrintUtil.isValidConfiguration( attributes, report );
    assertThat( result, is( equalTo( Java14PrintUtil.CONFIGURATION_VALID ) ) );
  }

  @Test
  public void testCopyAuxillaryAttributes() {
    MasterReport report = mockReport( PageFormat.LANDSCAPE );

    PrintRequestAttributeSet result = Java14PrintUtil.copyAuxillaryAttributes( null, report );
    verifyCopyAuxillaryResult( result );

    PrintRequestAttributeSet existingResult = Java14PrintUtil.copyAuxillaryAttributes( result, report );
    verifyCopyAuxillaryResult( existingResult );
  }

  private void verifyCopyAuxillaryResult( PrintRequestAttributeSet result ) {
    assertThat( result, is( notNullValue() ) );
    assertThat( result.get( JobName.class ), is( instanceOf( JobName.class ) ) );
    assertThat( ( (JobName) result.get( JobName.class ) ).getValue(), is( equalTo( JOB_NAME ) ) );
    assertThat( result.get( Copies.class ), is( instanceOf( Copies.class ) ) );
    assertThat( ( (Copies) result.get( Copies.class ) ).getValue(), is( equalTo( 1 ) ) );
  }

  @Test
  public void testExtractPageFormat() {
    verifyPageFormat( PageFormat.PORTRAIT );
    verifyPageFormat( PageFormat.LANDSCAPE );
    verifyPageFormat( PageFormat.REVERSE_LANDSCAPE );
  }

  private void verifyPageFormat( int orientation ) {
    MasterReport report = mockReport( orientation );
    PrintRequestAttributeSet attributeSet = Java14PrintUtil.copyConfiguration( null, report );
    PageFormat result = Java14PrintUtil.extractPageFormat( attributeSet );
    assertThat( result, is( notNullValue() ) );
    assertThat( result.getOrientation(), is( equalTo( orientation ) ) );
  }

  @Test( expected = PrintException.class )
  public void testPrintDirectlyException() throws Exception {
    MasterReport report = mockReport( PageFormat.LANDSCAPE );
    PrintService printService = mock( PrintService.class );

    doReturn( false ).when( printService ).isDocFlavorSupported( DocFlavor.SERVICE_FORMATTED.PAGEABLE );

    Java14PrintUtil.printDirectly( report, printService );
  }

  @Test
  public void testPrintDirectly() throws Exception {
    MasterReport report = mockReport( PageFormat.LANDSCAPE );
    PrintService printService = mock( PrintService.class );
    DocPrintJob job = mock( DocPrintJob.class );

    ArgumentCaptor<SimpleDoc> docCaptor = ArgumentCaptor.forClass( SimpleDoc.class );
    ArgumentCaptor<PrintRequestAttributeSet> attrsCaptor = ArgumentCaptor.forClass( PrintRequestAttributeSet.class );

    doReturn( true ).when( printService ).isDocFlavorSupported( DocFlavor.SERVICE_FORMATTED.PAGEABLE );
    doReturn( job ).when( printService ).createPrintJob();
    doNothing().when( job ).print( docCaptor.capture(), attrsCaptor.capture() );

    Java14PrintUtil.printDirectly( report, printService );

    verify( job ).print( any( SimpleDoc.class ), any( PrintRequestAttributeSet.class ) );
    assertThat( docCaptor.getValue().getPrintData(), is( instanceOf( PrintReportProcessor.class ) ) );
    assertThat( docCaptor.getValue().getDocFlavor(), is( instanceOf( DocFlavor.SERVICE_FORMATTED.class ) ) );
    assertThat( attrsCaptor.getValue().size(), is( equalTo( 5 ) ) );
  }

}
