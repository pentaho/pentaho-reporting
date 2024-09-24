package extensions.datasources.scriptable;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactoryModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInfo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScriptableDataFactoryModuleIT {

  private static final String DATA_FACTORY_ID =
      "org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory";

  @Test
  public void testInitialize() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ScriptableDataFactoryModule module = new ScriptableDataFactoryModule();
    module.initialize( null );

    assertThat( module.getDescription(), is( equalTo( "test description" ) ) );
    assertThat( module.getMajorVersion(), is( equalTo( "1" ) ) );
    assertThat( module.getMinorVersion(), is( equalTo( "1" ) ) );
    assertThat( module.getPatchLevel(), is( equalTo( "0" ) ) );
    assertThat( module.getName(), is( equalTo( "test-module-name" ) ) );
    assertThat( module.getProducer(), is( equalTo( "test producer" ) ) );

    ModuleInfo[] requiredModules = module.getRequiredModules();
    assertThat( requiredModules.length, is( equalTo( 1 ) ) );
    ModuleInfo requiredModule = requiredModules[0];
    assertThat( requiredModule.getModuleClass(), is( equalTo( "test.required.module.class" ) ) );
    assertThat( requiredModule.getMinorVersion(), is( equalTo( "2" ) ) );
    assertThat( requiredModule.getMajorVersion(), is( equalTo( "2" ) ) );
    assertThat( requiredModule.getPatchLevel(), is( equalTo( "1" ) ) );

    ModuleInfo[] optionalModules = module.getOptionalModules();
    assertThat( optionalModules.length, is( equalTo( 1 ) ) );
    ModuleInfo optionalModule = optionalModules[0];
    assertThat( optionalModule.getModuleClass(), is( equalTo( "test.optional.module.class" ) ) );
    assertThat( optionalModule.getMinorVersion(), is( equalTo( "1" ) ) );
    assertThat( optionalModule.getMajorVersion(), is( equalTo( "1" ) ) );
    assertThat( optionalModule.getPatchLevel(), is( equalTo( "0" ) ) );

    assertThat( DataFactoryRegistry.getInstance().isRegistered( DATA_FACTORY_ID ), is( equalTo( true ) ) );
    DataFactoryMetaData meta = DataFactoryRegistry.getInstance().getMetaData( DATA_FACTORY_ID );
    assertThat(
        meta.getBundleLocation(),
        is( equalTo( "org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactoryBundle" ) ) );
    assertThat( meta.isExpert(), is( equalTo( true ) ) );
  }
}
