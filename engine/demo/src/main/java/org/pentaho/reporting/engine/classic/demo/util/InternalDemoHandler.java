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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.MasterReport;

/**
 * A demo handler allows the generic use of demos in the framework.
 * <p/>
 * Every demo has a name, a way to create a report and a description in HTML documenting the demo. A demo also provides
 * a presentation component to either show the data or control the demo's appearance.
 *
 * @author Thomas Morgner
 */
public interface InternalDemoHandler extends DemoHandler
{
  /**
   * Returns the display name of the demo.
   *
   * @return the name.
   */
  public String getDemoName();

  /**
   * Assigns a demo controler to this demo. It is guaranteed, that a controler is set, before the presentation component
   * is queried or a report is created.
   *
   * @param controler the controler.
   */
  public void setController(final DemoController controler);

  /**
   * Returns the demo controler for this demo. The demo controler is supplied by the user of the demo handler.
   *
   * @return the demo controler for this demo handler.
   */
  public DemoController getController();

  /**
   * Creates the report. For XML reports, this will most likely call the ReportGenerator, while API reports may use this
   * function to build and return a new, fully initialized report object.
   *
   * @return the fully initialized JFreeReport object.
   * @throws ReportDefinitionException if an error occured preventing the report definition.
   */
  public MasterReport createReport() throws ReportDefinitionException;

  /**
   * Returns the URL of the HTML document describing this demo.
   *
   * @return the demo description.
   */
  public URL getDemoDescriptionSource();

  /**
   * Returns the presentation component for this demo. This component is shown before the real report generation is
   * started. Ususally it contains a JTable with the demo data and/or input components, which allow to configure the
   * report.
   *
   * @return the presentation component, never null.
   */
  public JComponent getPresentationComponent();
}
