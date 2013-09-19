package org.pentaho.reporting.designer.core.auth;

import java.util.Properties;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public interface AuthenticationStore
{
  public static final String USER_KEY = "user";
  public static final String PASSWORD_KEY = "password";
  public static final String TIMEOUT_KEY = "timeout";

  public String getUsername(String url);
  public String getPassword(String url);
  public String getOption(final String url, final String key);
  public String[] getDefinedOptions(String url);
  public String[] getKnownURLs();

  public AuthenticationData getCredentials(String url);
  public void add(final AuthenticationData authenticationData, final boolean persist);
  public void addCredentials(final String url,
                             final String user,
                             final String password,
                             final Properties options,
                             final boolean persist);
  
  public void removeCredentials(final String url);

  public int getIntOption(String path, String key, int defaultValue);

}
