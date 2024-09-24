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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource;

import javax.swing.table.AbstractTableModel;

/**
 * A TableModel containing (hard-coded) data about Java projects that are free / open source.
 * <p/>
 * If you would like to have your project listed here (and also at http://www.object-refinery.com/open.html), please
 * send an e-mail to David Gilbert at:
 * <p/>
 * david.gilbert@object-refinery.com
 *
 * @author David Gilbert
 */
public class OpenSourceProjects extends AbstractTableModel
{
  /**
   * The number of projects.
   */
  private final int projectCount;

  /**
   * Storage for the project categories.
   */
  private final String[] category;

  /**
   * Storage for the project names.
   */
  private final String[] name;

  /**
   * Storage for the project descriptions.
   */
  private final String[] description;

  /**
   * Storage for the project licences.
   */
  private final String[] licence;

  /**
   * Storage for the project URLs.
   */
  private final String[] url;

  /**
   * Creates a new TableModel, populated with data about various software projects.
   */
  public OpenSourceProjects()
  {
    this.projectCount = 19;
    this.category = new String[this.projectCount];
    this.name = new String[this.projectCount];
    this.description = new String[this.projectCount];
    this.licence = new String[this.projectCount];
    this.url = new String[this.projectCount];
    initialiseData();
  }

  /**
   * Returns the row count.
   *
   * @return the row count.
   */
  public int getRowCount()
  {
    return this.projectCount;
  }

  /**
   * Returns the column count.
   *
   * @return the column count.
   */
  public int getColumnCount()
  {
    return 5;
  }

  /**
   * Returns the column name.
   *
   * @param column the column (zero-based index).
   * @return the column name.
   */
  public String getColumnName(final int column)
  {
    switch (column)
    {
      case 0:
        return "Category";
      case 1:
        return "Name";
      case 2:
        return "Description";
      case 3:
        return "URL";
      case 4:
        return "Licence";
      default:
        throw new IllegalArgumentException("OpenSourceProjects: invalid column index.");
    }
  }

  /**
   * Returns the value at a particular row and column.
   *
   * @param row    the row (zero-based index).
   * @param column the column (zero-based index).
   * @return the value.
   */
  public Object getValueAt(final int row, final int column)
  {
    if (column == 0)
    {
      return this.category[row];
    }
    else if (column == 1)
    {
      return this.name[row];
    }
    else if (column == 2)
    {
      return this.description[row];
    }
    else if (column == 3)
    {
      return this.url[row];
    }
    else if (column == 4)
    {
      return this.licence[row];
    }
    else
    {
      return null;
    }
  }

  /**
   * Initialises the data.  This has been hard-coded for demo purposes only.  Typically you would read data from a
   * database, a file or some other data source.
   */
  private void initialiseData()
  {
    int c = 0;

    this.category[c] = "Applications";
    this.name[c] = "JDictionary";
    this.description[c] = "A powerful multi platform dictionary application.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://jdictionary.info";

    c += 1;
    this.category[c] = "Applications";
    this.name[c] = "JFtp";
    this.description[c] = "A Swing-based FTP client.";
    this.licence[c] = "GPL";
    this.url[c] = "http://sourceforge.net/projects/j-ftp";

    c += 1;
    this.category[c] = "Applications";
    this.name[c] = "Pooka";
    this.description[c] = "An e-mail client written using the Javamail API.";
    this.licence[c] = "GPL";
    this.url[c] = "http://suberic.net/pooka";

    c += 1;
    this.category[c] = "IDEs";
    this.name[c] = "Eclipse";
    this.description[c] = "\"a kind of universal tool platform - an open extensible IDE for "
        + "anything and nothing in particular.\"";
    this.licence[c] = "IBM Public Licence (BSD Style, with extra rights granted for IBM)";
    this.url[c] = "http://www.eclipse.org";

    c += 1;
    this.category[c] = "IDEs";
    this.name[c] = "NetBeans";
    this.description[c] = "Sun's open source IDE.";
    this.licence[c] = "Sun Public Licence (BSD Style, with extra rights granted for Sun "
        + "Microsystems)";
    this.url[c] = "http://www.netbeans.org";

    c += 1;
    this.category[c] = "Project Tools";
    this.name[c] = "Checkstyle";
    this.description[c] = "A development tool to help programmers write Java code that "
        + "adheres to a coding standard.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://checkstyle.sourceforge.net";

    c += 1;
    this.category[c] = "Other Development Tools";
    this.name[c] = "Ant";
    this.description[c] = "An extremely powerful Java-based project build tool.";
    this.licence[c] = "Apache License";
    this.url[c] = "http://jakarta.apache.org";

    c += 1;
    this.category[c] = "Other Development Tools";
    this.name[c] = "JUnit";
    this.description[c] = "A unit testing tool.";
    this.licence[c] = "IBM Common Public License";
    this.url[c] = "http://www.junit.org";

    c += 1;
    this.category[c] = "Class Libraries";
    this.name[c] = "JFreeChart";
    this.description[c] = "A free Java chart library.  JFreeChart can be used in applications, "
        + "applets, servlets and JSP.  It can generate pie charts (2D and 3D), bar charts "
        + "(horizontal or vertical, stacked or regular), line charts, time series charts, "
        + "high/low/open/close charts, candlestick plots, moving averages, scatter plots, "
        + "Gantt charts, thermometers, dials, combination charts and more.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://www.jfree.org/jfreechart";

    c += 1;
    this.category[c] = "Class Libraries";
    this.name[c] = "JFreeReport";
    this.description[c] = "A free Java report library.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://www.jfree.org/jfreereport";

    c += 1;
    this.category[c] = "Class Libraries";
    this.name[c] = "iText";
    this.description[c] = "For generating PDF content.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://www.lowagie.com/iText";

    c += 1;
    this.category[c] = "Class Libraries";
    this.name[c] = "Batik";
    this.description[c] = "Batik is a Java(tm) technology based toolkit for applications or "
        + "applets that want to use images in the Scalable Vector Graphics "
        + "(SVG) format for various purposes, such as viewing, generation or "
        + "manipulation.";
    this.licence[c] = "Apache License";
    this.url[c] = "http://xml.apache.org";

    c += 1;
    this.category[c] = "Class Libraries";
    this.name[c] = "JasperReports";
    this.description[c] = "For generating reports from JDBC.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://jasperreports.sourceforge.net";

    c += 1;
    this.category[c] = "Class Libraries";
    this.name[c] = "JGraph";
    this.description[c] = "For presenting and manipulating graphs.";
    this.licence[c] = "LGPL";
    this.url[c] = "http://www.jgraph.com";

    c += 1;
    this.category[c] = "Text Editors";
    this.name[c] = "JEdit";
    this.description[c] = "A programmer's text editor, with a multitude of plug-ins available.";
    this.licence[c] = "GPL";
    this.url[c] = "http://www.jedit.org";

    c += 1;
    this.category[c] = "Text Editors";
    this.name[c] = "Jext";
    this.description[c] = "Another popular editor.";
    this.licence[c] = "GPL";
    this.url[c] = "http://www.jext.org";

    c += 1;
    this.category[c] = "Server Side";
    this.name[c] = "Cewolf";
    this.description[c] = "A JSP tag library for charts (using JFreeChart).";
    this.licence[c] = "LGPL";
    this.url[c] = "http://cewolf.sourceforge.net";

    c += 1;
    this.category[c] = "Server Side";
    this.name[c] = "JBoss";
    this.description[c] = "An Open Source, standards-compliant, application server "
        + "implemented in 100% Pure Java and distributed for free.";
    this.licence[c] = "GPL";
    this.url[c] = "http://www.jboss.org";

    c += 1;
    this.category[c] = "Server Side";
    this.name[c] = "Tapestry";
    this.description[c] = "A powerful, open-source, all-Java framework for creating leading "
        + "edge web applications in Java.";
    this.licence[c] = "GPL";
    this.url[c] = "http://jakarta.apache.org/tapestry";

  }

}
