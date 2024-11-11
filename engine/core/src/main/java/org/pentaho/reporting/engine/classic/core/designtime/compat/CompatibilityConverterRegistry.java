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


package org.pentaho.reporting.engine.classic.core.designtime.compat;

import java.util.ArrayList;
import java.util.Arrays;

public class CompatibilityConverterRegistry {
  private static final CompatibilityConverterRegistry instance = new CompatibilityConverterRegistry();
  private ArrayList<Class<? extends CompatibilityConverter>> converters;

  public static CompatibilityConverterRegistry getInstance() {
    return instance;
  }

  public CompatibilityConverterRegistry() {
    this.converters = new ArrayList<Class<? extends CompatibilityConverter>>();
    this.converters.add( LayoutCompatibility_3_9_Converter.class );
    this.converters.add( LayoutCompatibility_5_0_Converter.class );
  }

  public void register( final Class<? extends CompatibilityConverter> converter ) {
    this.converters.add( converter );
  }

  public CompatibilityConverter[] getConverters() {
    try {
      final CompatibilityConverter[] retval = new CompatibilityConverter[converters.size()];
      for ( int i = 0; i < converters.size(); i++ ) {
        final Class<? extends CompatibilityConverter> compatibilityConverterClass = converters.get( i );
        retval[i] = compatibilityConverterClass.newInstance();
      }
      Arrays.sort( retval, new CompatibilityConverterComparator() );
      return retval;
    } catch ( InstantiationException e ) {
      throw new IllegalStateException( e );
    } catch ( IllegalAccessException e ) {
      throw new IllegalStateException( e );
    }
  }
}
