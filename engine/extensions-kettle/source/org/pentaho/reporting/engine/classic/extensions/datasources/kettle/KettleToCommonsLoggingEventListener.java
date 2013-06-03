package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.KettleLoggingEventListener;


/**
 * This class listens to all Kettle logging and passes it to Apache Commons Logging
 *  
 * @author Matt Casters
 *
 */
public class KettleToCommonsLoggingEventListener implements KettleLoggingEventListener {

  private static Log logger = LogFactory.getLog(KettleToCommonsLoggingEventListener.class);

  @Override
  public void eventAdded(final KettleLoggingEvent loggingEvent) {
    // The level mentioned below is the Kettle logging level.
    //
    switch (loggingEvent.getLevel()) {
      case NOTHING:
        break;
      case ROWLEVEL:
      case DEBUG:
        logger.debug(loggingEvent.getMessage());
        break;
      case ERROR:
        logger.error(loggingEvent.getMessage());
        break;
      default:
        logger.info(loggingEvent.getMessage());
        break;
    }

  }
}
