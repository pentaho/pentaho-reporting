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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

import java.io.OutputStream;

public class ReportProcessTaskUtil {
  private ReportProcessTaskUtil() {
  }

  public static void configureBodyStream( final ReportProcessTask task, final OutputStream outputStream,
      final String fileName, final String suffix ) {
    if ( task == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }
    if ( fileName == null ) {
      throw new NullPointerException();
    }

    final StreamRepository streamRepository = new StreamRepository( new NoCloseOutputStream( outputStream ) );
    final ContentLocation targetRoot = streamRepository.getRoot();

    task.setBodyContentLocation( targetRoot );
    task.setBodyNameGenerator( new DefaultNameGenerator( targetRoot, fileName, suffix ) );
  }
}
