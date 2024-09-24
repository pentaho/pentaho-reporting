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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.KettleLoggingEventListener;


/**
 * This class listens to all Kettle logging and passes it to Apache Commons Logging
 *
 * @author Matt Casters
 */
public class KettleToCommonsLoggingEventListener implements KettleLoggingEventListener {

  private static Log logger = LogFactory.getLog( KettleToCommonsLoggingEventListener.class );

  @Override
  public void eventAdded( final KettleLoggingEvent loggingEvent ) {
    // The level mentioned below is the Kettle logging level.
    //
    switch( loggingEvent.getLevel() ) {
      case NOTHING:
        break;
      case ROWLEVEL:
      case DEBUG:
        logger.debug( loggingEvent.getMessage() );
        break;
      case ERROR:
        logger.error( loggingEvent.getMessage() );
        break;
      default:
        logger.info( loggingEvent.getMessage() );
        break;
    }

  }
}
