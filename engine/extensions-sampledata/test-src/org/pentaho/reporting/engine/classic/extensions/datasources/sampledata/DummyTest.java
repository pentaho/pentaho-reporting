package org.pentaho.reporting.engine.classic.extensions.datasources.sampledata;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.boot.PackageManager;

public class DummyTest extends TestCase {
  public void testNothing() {
    ClassicEngineBoot.getInstance().start();
    PackageManager packageManager = ClassicEngineBoot.getInstance().getPackageManager();
    Module[] activeModules = packageManager.getActiveModules();
    boolean found = false;
    for ( Module activeModule : activeModules ) {
      if ( SampleDataModule.class.getName().equals( activeModule.getModuleClass() ) ) {
        found = true;
      }
    }
    assertTrue( found );
  }
}
