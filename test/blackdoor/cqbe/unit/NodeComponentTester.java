package blackdoor.cqbe.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import blackdoor.cqbe.settings.ConfigUnitTest;
import blackdoor.cqbe.node.server.ServerUnitTest;
import blackdoor.cqbe.output_logic.RouterTest;

@RunWith(Suite.class)
@SuiteClasses({ConfigUnitTest.class, ServerUnitTest.class, RouterTest.class})
public class NodeComponentTester {

}
