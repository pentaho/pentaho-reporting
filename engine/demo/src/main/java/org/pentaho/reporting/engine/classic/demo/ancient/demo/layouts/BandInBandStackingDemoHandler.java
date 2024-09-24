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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.layouts;

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.gui.pdf.PdfExportDialog;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.SimpleDemoFrame;
import org.pentaho.reporting.libraries.base.util.FloatDimension;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * A sample to show the band in band capabilities of JFreeReport ...
 *
 * @author Thomas Morgner.
 */
public class BandInBandStackingDemoHandler extends AbstractDemoHandler
{
  protected static final Log logger = LogFactory.getLog(BandInBandStackingDemoHandler.class);

  /**
   * An expression that returns a very complex component.
   */
  private static class ComplexComponentExpression extends AbstractExpression
  {
    /**
     * A component.
     */
    private transient Component pif;

    /**
     * Creates an expression.
     *
     * @param name the name.
     */
    protected ComplexComponentExpression(final String name)
    {
      setName(name);
      try
      {
        final PdfExportDialog dlg = new PdfExportDialog();
        pif = dlg.getContentPane();
        pif.setVisible(true);
        // remove the old content pane from the dialog, so that it has no
        // parent ...
        dlg.setContentPane(new JPanel());
      }
      catch (Exception e)
      {
        logger.error("PDFDialogInitialization failed");
      }
    }

    /**
     * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
     * original function. Only the datarow may be shared.
     *
     * @return a copy of this function.
     */
    public Expression getInstance()
    {
      return new ComplexComponentExpression(getName());
    }

    /**
     * Return the current expression value. <P> The value depends (obviously) on the expression implementation.
     *
     * @return the value of the function.
     */
    public Object getValue()
    {
      return pif;
    }

    private void readObject(final ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
      final PdfExportDialog dlg = new PdfExportDialog();
      pif = dlg.getContentPane();
      pif.setVisible(true);
    }
  }

  /**
   * Default constructor.
   */
  public BandInBandStackingDemoHandler()
  {
  }

  public String getDemoName()
  {
    return "Band in Band Stacking Demo";
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative("band-in-band.html", BandInBandStackingDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return new JPanel();
  }

  /**
   * create a band. The band contains a rectangle shape element in that band with the same boundries as the band.
   *
   * @param name   An optional name
   * @param color  the color of the rectangle element
   * @param x      the x coordinates
   * @param y      the y coordinates
   * @param width  the width of the band and the rectangle
   * @param height the height of the band and the rectangle
   * @return the created band
   */
  private Band createBand(final String name, final Color color,
                          final int x, final int y, final int width, final int height)
  {
    final Band band = new Band();
    band.setName("Band-" + name);
    band.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(width));
    band.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(height));
    band.getStyle().setStyleProperty(ElementStyleKeys.MAX_WIDTH, new Float(width));
    band.getStyle().setStyleProperty(ElementStyleKeys.MAX_HEIGHT, new Float(height));

    band.getStyle().setStyleProperty(ElementStyleKeys.POS_X, new Float(x));
    band.getStyle().setStyleProperty(ElementStyleKeys.POS_Y, new Float(y));

    // create the marker shape, the shape fills the generated band and paints the colored background
    // all coordinates or dimensions are within the band, and not affected by the bands placement in
    // the outer report bands
    band.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, color);
    return band;
  }

  /**
   * Create a report with a single report header band. This band contains several sub bands.
   *
   * @return the created report.
   */
  public MasterReport createReport()
  {
    final Band levelA1 = createBand("A1", Color.magenta, 0, 0, 100, 100);
    levelA1.addElement(createBand("A1-B1", Color.blue, 0, 50, 50, 50));
    levelA1.addElement(createBand("A1-B2", Color.yellow, 50, 0, 150, 50));
    // x=55%, y=5%, width=40%, height=100%
    final Band levelA2 = createBand("A2", Color.green, -50, 0, -50, -100);
    // x=5%, y=55%, width=40%, height=40%
    levelA2.addElement(createBand("A2-B1", Color.red, 0, -50, -50, -50));
    // x=55%, y=5%, width=40%, height=40%
    levelA2.addElement(createBand("A2-B2", Color.darkGray, -55, -5, -40, -40));

    final ReportHeader header = new ReportHeader();
    header.setName("Report-Header");
    header.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(-100));
    header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(100));
    header.getStyle().setStyleProperty(ElementStyleKeys.MAX_WIDTH, new Float(Short.MAX_VALUE));
    header.getStyle().setStyleProperty(ElementStyleKeys.MAX_HEIGHT, new Float(100));

    header.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, Color.ORANGE);

    header.addElement(levelA1);
    header.addElement(levelA2);

    final ContentFieldElementFactory cfef = new ContentFieldElementFactory();
    cfef.setFieldname("CreateComponent");
    cfef.setMinimumSize(new FloatDimension(400, 400));
    cfef.setAbsolutePosition(new Point2D.Float(0, 0));

    final ReportFooter footer = new ReportFooter();
    footer.addElement(cfef.createElement());

    final MasterReport report = new MasterReport();
    report.setReportHeader(header);
    report.setReportFooter(footer);
    report.setName("Band in Band stacking");

    report.addExpression(new ComplexComponentExpression("CreateComponent"));
    return report;
  }


  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();

    final BandInBandStackingDemoHandler demoHandler = new BandInBandStackingDemoHandler();
    final SimpleDemoFrame frame = new SimpleDemoFrame(demoHandler);
    frame.init();
    frame.pack();
    LibSwingUtil.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }
}
