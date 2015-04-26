package blackdoor.cqbe.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import blackdoor.cqbe.settings.ConfigUnitTest;
import blackdoor.cqbe.node.server.NodeServerUnitTest;
import blackdoor.cqbe.output_logic.RouterTest;

@RunWith(Suite.class)
@SuiteClasses({ConfigUnitTest.class, NodeServerUnitTest.class, RouterTest.class})
public class NodeComponentTester {

}
