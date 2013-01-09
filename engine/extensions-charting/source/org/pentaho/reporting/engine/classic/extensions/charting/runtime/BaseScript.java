/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pentaho.reporting.engine.classic.extensions.charting.runtime;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author pdpi
 */
public abstract class BaseScript implements Script
{
  private static final Log logger = LogFactory.getLog(BaseScript.class);
  private String source;
  private String rootPath;
  private Scriptable scope;

  BaseScript()
  {
  }

  BaseScript(final String source)
  {
    this.source = source.replaceAll("\\\\", "/").replaceAll("/+", "/");
    this.rootPath = this.source.replaceAll("(.*/).*", "$1");
  }

  public void initializeObjects()
  {
    final Context context = ContextFactory.getGlobal().enterContext();
    try
    {
      final Object wrappedFactory = Context.javaToJS(new DatasourceFactory(), scope);
      ScriptableObject.putProperty(scope, "datasourceFactory", wrappedFactory);
    }
    finally
    {
      context.exit();
    }
  }

  public void setScope(final Scriptable scope)
  {
    this.scope = scope;
    if (scope instanceof BaseScope)
    {
      final BaseScope baseScope = (BaseScope) scope;
      baseScope.setBasePath(this.rootPath);
    }
    initializeObjects();
  }

  protected void executeScript(final Map<String, Object> params)
  {
    final Context cx = Context.getCurrentContext();
    // env.js has methods that pass the 64k Java limit, so we can't compile
    // to bytecode. Interpreter mode to the rescue!
    cx.setOptimizationLevel(-1);
    cx.setLanguageVersion(Context.VERSION_1_7);

    final Object wrappedParams;
    if (params != null)
    {
      wrappedParams = Context.javaToJS(params, scope);
    }
    else
    {
      wrappedParams = Context.javaToJS(new HashMap<String, Object>(), scope);
    }

    try
    {
      ScriptableObject.defineProperty(scope, "params", wrappedParams, 0);
      cx.evaluateReader(scope, new FileReader(source), this.source, 1, null);
    }
    catch (IOException ex)
    {
      logger.error("Failed to read " + source + ": " + ex.toString());
    }
  }
}
