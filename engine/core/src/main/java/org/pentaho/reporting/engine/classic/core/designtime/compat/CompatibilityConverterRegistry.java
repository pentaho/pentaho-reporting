/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
