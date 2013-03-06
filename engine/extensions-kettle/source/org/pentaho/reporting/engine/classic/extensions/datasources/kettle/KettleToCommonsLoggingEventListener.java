package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.KettleLoggingEventListener;

import common.Logger;

/**
 * This class listens to all Kettle logging and passes it to Apache Commons Logging
 *  
 * @author Matt Casters
 *
 */
public class KettleToCommonsLoggingEventListener implements KettleLoggingEventListener {

  private static Logger logger = Logger.getLogger(KettleToCommonsLoggingEventListener.class);

  @Override
  public void eventAdded(KettleLoggingEvent loggingEvent) {
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
