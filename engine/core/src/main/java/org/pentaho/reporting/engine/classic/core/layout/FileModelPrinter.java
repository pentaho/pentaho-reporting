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


package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileModelPrinter extends ModelPrinter {
  private BufferedWriter writer;
  private boolean enablePageFooter;
  private boolean enablePageHeader;

  public FileModelPrinter() throws IOException {
    this( "model-dump-" );
    this.enablePageFooter = true;
    this.enablePageHeader = true;
  }

  public FileModelPrinter( final String prefix ) throws IOException {
    final File file = File.createTempFile( prefix, ".txt" );
    writer = new BufferedWriter( new FileWriter( file ) );
  }

  public FileModelPrinter( final String prefix, final File tempDir ) throws IOException {
    final File file = File.createTempFile( prefix, ".txt", tempDir );
    writer = new BufferedWriter( new FileWriter( file ) );
  }

  public FileModelPrinter( final File file ) throws IOException {
    writer = new BufferedWriter( new FileWriter( file ) );
  }

  protected boolean isPrintingEnabled() {
    return true;
  }

  protected void print( final String s ) {
    try {
      writer.write( s );
      writer.write( "\n" );
    } catch ( IOException e ) {
      throw new IllegalStateException( e );
    }
  }

  public void close() throws IOException {
    writer.close();
  }

  protected boolean isPrintPageHeader() {
    return enablePageHeader;
  }

  protected boolean isPrintPageFooter() {
    return enablePageFooter;
  }

  public static void print( final String filename, final RenderBox renderBox ) {
    try {
      final FileModelPrinter f = new FileModelPrinter( new File( filename ) );
      f.enablePageFooter = false;
      f.enablePageHeader = false;
      try {
        f.print( renderBox );
      } finally {
        f.close();
      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }

  }
}
