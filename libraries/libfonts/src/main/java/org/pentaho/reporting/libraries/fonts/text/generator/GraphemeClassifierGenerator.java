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


package org.pentaho.reporting.libraries.fonts.text.generator;

import org.pentaho.reporting.libraries.fonts.text.GraphemeClassifier;
import org.pentaho.reporting.libraries.fonts.tools.ByteTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Creation-Date: 27.05.2007, 17:50:09
 *
 * @author Thomas Morgner
 * @noinspection UseOfSystemOutOrSystemErr
 */
public class GraphemeClassifierGenerator {
  private static final int MAX_CHARS = 0x110000;
  private static final int MAX_RANGES = 0x1100;

  private GraphemeClassifierGenerator() {
  }

  public static void main( final String[] args ) throws IOException {
    if ( args.length < 2 ) {
      System.out.println( "Give the mapping files as first parameter and the target-file as second-parameter." );
      System.exit( 1 );
    }

    final File f = new File( args[ 0 ] );
    if ( f.isFile() == false || f.exists() == false || f.canRead() == false ) {
      System.out.println( "Mapping file is not valid: " + f );
      System.exit( 1 );
    }

    final File target = new File( args[ 1 ] );
    if ( target.exists() && target.canWrite() == false ) {
      System.out.println( "Target file is not valid: " + target );
      System.exit( 1 );
    }

    final ByteTable table = new ByteTable( MAX_RANGES, 256 );
    final BufferedReader reader = new BufferedReader( new FileReader( f ) );
    String line;
    while ( ( line = reader.readLine() ) != null ) {
      if ( line.length() > 0 && line.charAt( 0 ) == '#' ) {
        continue;
      }
      if ( line.trim().length() == 0 ) {
        continue;
      }
      final int separator = line.indexOf( ';' );
      if ( separator < 1 ) {
        continue;
      }

      int comment = line.indexOf( '#' );
      if ( comment == -1 ) {
        comment = line.length();
      }

      final String chars = line.substring( 0, separator ).trim();
      final String classification = line.substring( separator + 1, comment ).trim();

      final int range = chars.indexOf( ".." );
      if ( range == -1 ) {
        final int idx = Integer.parseInt( chars, 16 );
        final int targetRange = idx >> 8;
        table.setByte( targetRange, idx & 0xFF, classify( classification ) );
      } else {
        final int startRange = Integer.parseInt( chars.substring( 0, range ), 16 );
        final int endRange = Integer.parseInt( chars.substring( range + 2 ), 16 );
        for ( int i = startRange; i < endRange; i++ ) {
          final int targetRange = i >> 8;
          table.setByte( targetRange, i & 0xFF, classify( classification ) );
        }
      }
    }

    final FileOutputStream targetStream = new FileOutputStream( target );
    final ObjectOutputStream oout = new ObjectOutputStream( targetStream );
    oout.writeObject( table );
    oout.flush();
    targetStream.close();
  }

  private static byte classify( final String text ) {
    if ( "CR".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.CR;
    }
    if ( "LF".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.LF;
    }
    if ( "Control".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.CONTROL;
    }
    if ( "Extend".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.EXTEND;
    }
    if ( "L".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.L;
    }
    if ( "LV".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.LV;
    }
    if ( "LVT".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.LVT;
    }
    if ( "V".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.V;
    }
    if ( "T".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.T;
    }
    if ( "Other".equalsIgnoreCase( text ) ) {
      return GraphemeClassifier.OTHER;
    }
    throw new IllegalStateException( "Parse Error: Classification is not known: " + text );
  }
}
