/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GroupPageBreakIT extends TestCase {
  public GroupPageBreakIT() {
  }

  public GroupPageBreakIT( final String s ) {
    super( s );
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  private MasterReport getReport() throws Exception {
    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Report Header" ) );

    report.getReportFooter().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Report Footer" ) );

    report.getPageHeader().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Page Header" ) );

    report.getPageFooter().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Page Footer" ) );

    report.getItemBand().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Item Band" ) );

    report.getRelationalGroup( 0 ).getHeader().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Group Header" ) );

    report.getRelationalGroup( 0 ).getFooter().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "Group Footer" ) );

    report.getRelationalGroup( 0 ).getHeader().getStyle()
        .setBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, true );
    report.getRelationalGroup( 0 ).getFooter().getStyle().setBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, true );

    return report;
  }

  public void testPageCount() throws Exception {
    final MasterReport report = getReport();
    final PrintReportProcessor proc = new PrintReportProcessor( report );
    assertEquals( 3, proc.getNumberOfPages() );
    proc.close();
  }

  public void testSmallPageCount() throws Exception {
    final MasterReport report = getReport();
    report.setReportFooter( new ReportFooter() );
    report.setReportHeader( new ReportHeader() );

    final PrintReportProcessor proc = new PrintReportProcessor( report );
    assertEquals( 1, proc.getNumberOfPages() );
    proc.close();
  }

  public MasterReport getReportTest2() {
    /**
     * <groups> <group name="CMR"> <fields> <field>CMR</field> </fields> <groupheader height="20"> </groupheader>
     * </group>
     *
     * <!-- Second group--> <group name="OC" > <fields> <field>CMR</field> <field>OC</field> </fields>
     *
     * <groupheader height="20" repeat="true"> ...................... </groupheader>
     *
     * <groupfooter height="10" pagebreak-after-print="true"> .................... </groupfooter> </group> </groups>
     *
     * <items height="95" fontname="Serif" fontstyle="plain" fontsize="10">
     */
    final MasterReport report = new MasterReport();
    final RelationalGroup cmr = new RelationalGroup();
    cmr.addField( "CMR" );
    cmr.getHeader().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "CMR header" ) );
    cmr.getHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.ORANGE );

    report.addGroup( cmr );

    final RelationalGroup oc = new RelationalGroup();
    oc.addField( "CMR" );
    oc.addField( "OC" );
    oc.getHeader().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    oc.getHeader().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "CMR-OC header" ) );
    oc.getHeader().getStyle().setBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER, true );

    oc.getFooter().getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, Color.YELLOW );
    oc.getFooter().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "CMR-OC footer" ) );
    oc.getFooter().getStyle().setBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, true );
    report.addGroup( oc );

    report.getItemBand().addElement(
        LabelElementFactory.createLabelElement( null, new Rectangle2D.Float( 0, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "ItemBand" ) );
    report.getItemBand().addElement(
        TextFieldElementFactory.createStringElement( null, new Rectangle2D.Float( 200, 0, 150, 20 ), null,
            ElementAlignment.LEFT, null, "-", "RowIndicator" ) );
    report.setDataFactory( new TableDataFactory( "default", createTest2Model() ) );
    return report;
  }

  public TableModel createTest2Model() {
    final Object[][] data =
    { { "cmr1", "oc1", "Row0" }, { "cmr1", "oc2", "Row1" }, { "cmr2", "oc1", "Row2" }, { "cmr2", "oc2", "Row3" }, };

    final String[] names = new String[] { "CMR", "OC", "RowIndicator" };
    final DefaultTableModel model = new DefaultTableModel( data, names );
    return model;
  }

  public void testGroupReport2() throws Exception {
    final MasterReport report = getReportTest2();

    final PrintReportProcessor proc = new PrintReportProcessor( report );
    assertEquals( 4, proc.getNumberOfPages() );
    proc.close();
  }
}
