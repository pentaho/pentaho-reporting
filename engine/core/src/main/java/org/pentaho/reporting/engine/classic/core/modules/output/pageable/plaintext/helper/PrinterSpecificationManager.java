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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.PrinterDriver;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class PrinterSpecificationManager {
  private static final Log logger = LogFactory.getLog( PrinterSpecificationManager.class );

  private static class GenericPrinterSpecification implements PrinterSpecification {
    private PrinterEncoding genericEncoding;

    protected GenericPrinterSpecification() {
      genericEncoding = new PrinterEncoding( "ASCII", "ASCII", "ASCII", new byte[] { 0, 0 } );
    }

    public String getDisplayName() {
      return "Generic";
    }

    /**
     * Returns the encoding definition for the given java encoding.
     *
     * @param encoding
     *          the java encoding that should be mapped into a printer specific encoding.
     * @return the printer specific encoding.
     */
    public PrinterEncoding getEncoding( final String encoding ) {
      return genericEncoding;
    }

    /**
     * Returns the name of the encoding mapping. This is usually the same as the printer model name.
     *
     * @return the printer model.
     */
    public String getName() {
      return "Generic";
    }

    /**
     * Checks whether the given Java-encoding is supported.
     *
     * @param encoding
     *          the java encoding that should be mapped into a printer specific encoding.
     * @return true, if there is a mapping, false otherwise.
     */
    public boolean isEncodingSupported( final String encoding ) {
      return true;
    }

    /**
     * Returns true, if a given operation is supported, false otherwise.
     *
     * @param operationName
     *          the operation, that should be performed
     * @return true, if the printer will be able to perform that operation, false otherwise.
     */
    public boolean isFeatureAvailable( final String operationName ) {
      return true;
    }
  }

  private HashMap printerModels;
  private static PrinterSpecification generic;

  public PrinterSpecificationManager() {
    final PrinterSpecification generic = PrinterSpecificationManager.getGenericPrinter();
    printerModels = new HashMap();
    printerModels.put( generic.getName(), generic );
  }

  public void load( final String resourceName ) {
    if ( resourceName == null ) {
      throw new NullPointerException();
    }
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream( resourceName, PrinterDriver.class );
    if ( in == null ) {
      PrinterSpecificationManager.logger.error( "Printer definition is missing: " + resourceName );
      return;
    }
    try {
      try {
        load( in );
      } finally {
        in.close();
      }
    } catch ( IOException e ) {
      PrinterSpecificationManager.logger.error( "Unable to load printer definition file " + resourceName, e );
    }
  }

  public void load( final InputStream in ) throws IOException {
    if ( in == null ) {
      throw new NullPointerException();
    }

    final DefaultConfiguration encodingConfig = new DefaultConfiguration();
    encodingConfig.load( in );
    final PropertyPrinterSpecificationLoader loader = new PropertyPrinterSpecificationLoader();
    final PrinterEncoding[] encodings = loader.loadEncodings( encodingConfig );
    final PrinterSpecification[] printers = loader.loadPrinters( encodingConfig, encodings );

    // if there is a valid printer definition available, we do not
    // need a generic "Generic" printer. If one is required, it will
    // be defined by the specification file.
    if ( printers.length > 0 ) {
      printerModels.remove( PrinterSpecificationManager.getGenericPrinter().getName() );
    }
    for ( int i = 0; i < printers.length; i++ ) {
      addPrinter( printers[i] );
    }
  }

  private void addPrinter( final PrinterSpecification printer ) {
    if ( printer == null ) {
      throw new NullPointerException();
    }

    printerModels.put( printer.getName(), printer );
  }

  public String[] getPrinterNames() {
    return (String[]) printerModels.keySet().toArray( new String[printerModels.size()] );
  }

  public PrinterSpecification getPrinter( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    return (PrinterSpecification) printerModels.get( name );
  }

  public static synchronized PrinterSpecification getGenericPrinter() {
    if ( PrinterSpecificationManager.generic == null ) {
      PrinterSpecificationManager.generic = new PrinterSpecificationManager.GenericPrinterSpecification();
    }
    return PrinterSpecificationManager.generic;
  }
}
