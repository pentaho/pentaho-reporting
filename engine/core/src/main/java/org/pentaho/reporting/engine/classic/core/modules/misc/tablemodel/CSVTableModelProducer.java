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

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import org.pentaho.reporting.libraries.base.util.CSVTokenizer;

import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Creates a <code>TableModel</code> using a file formated in CSV for input. The separation can be what ever you want
 * (as it is an understandable regexp). The default separator is a <code>,</code>.
 *
 * @author Mimil
 */
public class CSVTableModelProducer {
  private BufferedReader reader;
  private String separator;
  private CSVTableModel tableModel;
  private boolean columnNameFirst;

  public CSVTableModelProducer( final InputStream in ) {
    this( new BufferedReader( new InputStreamReader( in ) ) );
  }

  public CSVTableModelProducer( final InputStream in, final String encoding ) throws UnsupportedEncodingException {
    this( new BufferedReader( new InputStreamReader( in, encoding ) ) );
  }

  public CSVTableModelProducer( final String filename ) throws FileNotFoundException {
    this( new BufferedReader( new FileReader( filename ) ) );
  }

  public CSVTableModelProducer( final BufferedReader r ) {
    if ( r == null ) {
      throw new NullPointerException( "The input stream must not be null" ); //$NON-NLS-1$
    }
    this.reader = r;
    this.separator = ","; //$NON-NLS-1$
  }

  public void close() throws IOException {
    this.reader.close();
  }

  /**
   * Parses the input and stores data in a TableModel.
   *
   * @see this.getTableModel()
   */
  public synchronized TableModel parse() throws IOException {
    if ( tableModel != null ) {
      return tableModel;
    }

    this.tableModel = new CSVTableModel();

    if ( this.columnNameFirst == true ) { // read the fisrt line
      final String first = this.reader.readLine();

      if ( first == null ) {
        // after the end of the file it makes no sense to read anything.
        // so we can safely return ..
        return tableModel;
      }
      this.tableModel.setColumnNames( splitLine( first, true ) );
    }

    final ArrayList data = new ArrayList();
    String line;
    int maxLength = 0;
    while ( ( line = this.reader.readLine() ) != null ) {
      final String[] o = splitLine( line, false );
      if ( o.length > maxLength ) {
        maxLength = o.length;
      }
      data.add( o );
    }

    close();

    final Object[][] array = new Object[data.size()][];
    data.toArray( array );
    this.tableModel.setData( array );
    return tableModel;
  }

  private String[] splitLine( final String line, final boolean trim ) {
    final ArrayList row = new ArrayList();
    final CSVTokenizer tokenizer = new CSVTokenizer( line, getSeparator(), "\"", trim );
    while ( tokenizer.hasMoreElements() ) {
      final String o = (String) tokenizer.nextElement();
      if ( trim ) {
        row.add( o.trim() );
      } else {
        row.add( o );
      }

    }
    return (String[]) row.toArray( new String[row.size()] );
  }

  /**
   * Returns the current separator used to parse the input.
   *
   * @return a regexp
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * Sets the separator for parsing the input. It can be a regexp as we use the function <code>String.split()</code>.
   * The default separator is a <code>;</code>.
   *
   * @param separator
   *          a regexp
   */
  public void setSeparator( final String separator ) {
    this.separator = separator;
  }

  /**
   * Creates the corrspondant TableModel of the input.
   *
   * @return the new TableModel
   */
  public TableModel getTableModel() throws IOException {
    return this.parse();
  }

  /**
   * Tells if the first line of the input was column names.
   *
   * @return boolean
   */
  public boolean isColumnNameFirstLine() {
    return columnNameFirst;
  }

  /**
   * Set if the first line of the input is column names or not.
   *
   * @param columnNameFirst
   *          boolean
   */
  public void setColumnNameFirstLine( final boolean columnNameFirst ) {
    this.columnNameFirst = columnNameFirst;
  }

}
