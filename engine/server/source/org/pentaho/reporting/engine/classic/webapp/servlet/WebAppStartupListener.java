package org.pentaho.reporting.engine.classic.webapp.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public class WebAppStartupListener implements ServletContextListener
{
  public WebAppStartupListener()
  {
  }

  public void contextInitialized(final ServletContextEvent servletContextEvent)
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void contextDestroyed(final ServletContextEvent servletContextEvent)
  {
    // we probably should kill EHCache's caches at this point ...
  }
}
