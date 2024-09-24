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

package org.pentaho.reporting.libraries.fonts.encoding.generator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.encoding.External8BitEncodingData;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * A simple sourcecode generator.
 *
 * @author Thomas Morgner
 */
public class EncodingGenerator {
  private static final Log logger = LogFactory.getLog( EncodingGenerator.class );

  private File targetDirectory;
  private DefaultConfiguration propertySet;
  private File sourceDirectory;
  public static final Integer ZERO = new Integer( 0 );

  public EncodingGenerator( final File targetDirectory,
                            final File sourceDirectory ) throws IOException {
    if ( targetDirectory == null ) {
      throw new NullPointerException();
    }
    if ( targetDirectory.isDirectory() == false ) {
      throw new IllegalArgumentException( "Not a directory: " + targetDirectory );
    }
    if ( targetDirectory.canWrite() == false ) {
      throw new IllegalArgumentException( "Not writable: " + targetDirectory );
    }
    if ( sourceDirectory == null ) {
      throw new NullPointerException();
    }
    if ( sourceDirectory.isDirectory() == false ) {
      throw new IllegalArgumentException( "Not a directory: " + sourceDirectory );
    }
    if ( sourceDirectory.canRead() == false ) {
      throw new IllegalArgumentException( "Not readable: " + sourceDirectory );
    }

    this.sourceDirectory = sourceDirectory;
    this.targetDirectory = targetDirectory;

    final InputStream propIn = ObjectUtilities.getResourceRelativeAsStream
      ( "encodings.properties", EncodingGenerator.class );
    propertySet = new DefaultConfiguration();
    try {
      propertySet.load( propIn );
    } finally {
      propIn.close();
    }
  }

  public void generatedFormatA( final Properties specifications,
                                final BufferedReader input,
                                final OutputStream output )
    throws IOException {
    if ( input == null ) {
      throw new NullPointerException();
    }
    if ( output == null ) {
      throw new NullPointerException();
    }
    if ( specifications == null ) {
      throw new NullPointerException();
    }

    final Integer[] data = new Integer[ 256 ];
    data[ 0 ] = ZERO;

    String s = input.readLine();
    while ( s != null ) {
      if ( s.length() > 0 && s.charAt( 0 ) == '#' ) {
        s = input.readLine();
        continue;
      }

      final StringTokenizer strtok = new StringTokenizer( s );
      if ( strtok.hasMoreTokens() == false ) {
        s = input.readLine();
        continue;
      }
      final String sourceChar = strtok.nextToken().trim();
      if ( sourceChar.length() == 0 ) {
        s = input.readLine();
        continue;
      }

      final Integer sourceVal = Integer.decode( sourceChar );
      if ( strtok.hasMoreTokens() ) {
        final String targetChar = strtok.nextToken();
        if ( ( targetChar.length() > 0 && targetChar.charAt( 0 ) == '#' ) == false ) {
          data[ sourceVal.intValue() ] = Integer.decode( targetChar );
        }
      }

      s = input.readLine();
    }

    final int[] indices = new int[ 256 ];
    final int[] values = new int[ 256 ];

    int index = 0;
    int prevIdx = 0;
    for ( int i = 1; i < data.length; i++ ) {
      final Integer integer = data[ i ];
      if ( integer == null ) {
        // no entry ... this means, we use the standard mapping ..
        continue;
      }

      final Integer prev = data[ prevIdx ];
      values[ index ] = integer.intValue() - prev.intValue();
      indices[ index ] = i - prevIdx;

      prevIdx = i;
      index += 1;
    }

    final ObjectOutputStream oout = new ObjectOutputStream( output );
    oout.writeObject( new External8BitEncodingData( indices, values ) );
    oout.flush();
  }

  public void generateAll() throws IOException {
    final Iterator keys = propertySet.keySet().iterator();
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      if ( key.startsWith( "encodings." ) == false ) {
        logger.info( "Encoding prefix not found." );
        continue;
      }
      if ( key.endsWith( ".Filename" ) == false ) {
        continue;
      }
      try {
        generate( key.substring( 0, key.length() - 8 ) );
      } catch ( Exception e ) {
        logger.warn( "Failed to generate Encoding " + key, e );
      }
    }
  }

  public void generate( final String prefix ) throws IOException {
    final String filename = propertySet.getConfigProperty( prefix + "Filename" );
    final String className = propertySet.getConfigProperty( prefix + "ClassName" );
    final String encoding = propertySet.getConfigProperty( prefix + "Encoding" );

    if ( filename == null ) {
      logger.info( "Not generating Encoding: " + prefix + ": Filename missing" );
      return;
    }
    if ( className == null ) {
      logger.info( "Not generating Encoding: " + prefix + ": ClassName missing" );
      return;
    }
    if ( encoding == null ) {
      logger.info( "Not generating Encoding: " + prefix + ": Encoding missing" );
      return;
    }

    final Properties properties = new Properties();
    properties.setProperty( "name", className );
    properties.setProperty( "source", filename );
    properties.setProperty( "encoding", encoding );

    final File sourceFile = new File( sourceDirectory, className + ".ser" );
    final File targetFile = new File( targetDirectory, filename );

    System.out.println( "Generated Java File:  " + sourceFile );
    System.out.println( "Source Specification: " + targetFile );
    final BufferedOutputStream bw =
      new BufferedOutputStream( new FileOutputStream( sourceFile ) );

    final BufferedReader input = new BufferedReader( new FileReader( targetFile ) );
    generatedFormatA( properties, input, bw );
    input.close();
    bw.close();
  }

  public void generatePropertyIndex()
    throws IOException {
    final Properties props = new Properties();
    final Iterator keys = propertySet.keySet().iterator();
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      if ( key.startsWith( "encodings." ) == false ) {
        logger.info( "Encoding prefix not found." );
        continue;
      }
      if ( key.endsWith( ".Filename" ) == false ) {
        continue;
      }
      try {
        generateEncodingEntry( props, key.substring( 0, key.length() - 8 ) );
      } catch ( Exception e ) {
        logger.warn( "Failed to generate Encoding " + key, e );
      }
    }

    final File sourceFile = new File( sourceDirectory, "encodings.properties" );
    final FileOutputStream fout = new FileOutputStream( sourceFile );
    props.store( fout, "Generated on " + new Date() );
    fout.close();
  }

  private void generateEncodingEntry( final Properties props, final String prefix ) {
    final String className = propertySet.getConfigProperty( prefix + "ClassName" );
    final String encoding = propertySet.getConfigProperty( prefix + "Encoding" );

    if ( className == null ) {
      logger.info( "Not generating Encoding: " + prefix + ": ClassName missing" );
      return;
    }
    if ( encoding == null ) {
      logger.info( "Not generating Encoding: " + prefix + ": Encoding missing" );
      return;
    }
    props.setProperty( encoding, className + ".ser" );
  }

  public static void main( final String[] args ) throws IOException {
    if ( args.length < 2 ) {
      throw new IllegalArgumentException( "Need two parameters: EncodingsDirectory and Source-Directory" );
    }

    final String encodingDirName = args[ 0 ];
    final String sourceDirName = args[ 1 ];

    LibFontBoot.getInstance().start();
    final File encodingsDir = new File( encodingDirName ).getAbsoluteFile();
    final File sourceDir = new File( sourceDirName ).getAbsoluteFile();
    final EncodingGenerator gen = new EncodingGenerator( encodingsDir, sourceDir );
    gen.generateAll();
    gen.generatePropertyIndex();
  }

}
