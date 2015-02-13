package blackdoor.cqbe.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import blackdoor.cqbe.settings.ConfigUnitTest;
import blackdoor.cqbe.node.server.ServerUnitTest;

@RunWith(Suite.class)
@SuiteClasses({ ConfigUnitTest.class, ServerUnitTest.class })
public class NodeComponentTester {

}
