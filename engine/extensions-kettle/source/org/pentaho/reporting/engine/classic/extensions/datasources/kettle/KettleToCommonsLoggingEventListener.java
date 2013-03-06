package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.di.core.logging.LoggingEvent;
import org.pentaho.di.core.logging.LoggingEventListener;

import common.Logger;

/**
 * This class listens to all Kettle logging and passes it to Apache Commons Logging
 *  
 * @author Matt Casters
 *
 */
public class KettleToCommonsLoggingEventListener implements LoggingEventListener {

  private static Logger logger = Logger.getLogger(KettleDataFactory.class);

  @Override
  public void eventAdded(LoggingEvent loggingEvent) {
    
    // The level mentioned below is the Kettle logging level.
    //
    switch(loggingEvent.getLevel()) {
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
