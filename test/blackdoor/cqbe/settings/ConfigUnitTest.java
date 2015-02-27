package blackdoor.cqbe.settings;

import static org.junit.Assert.*;

import java.io.File;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import blackdoor.cqbe.settings.ConfigurationException.*;
import blackdoor.cqbe.unit.TestAssistant;

/**
 * 
 * @author Cj Buresch
 * @version v0.1.0 - Jan 27, 2015
 */
public class ConfigUnitTest {

	private File defaultfile;
	private File settingsfile;
	private File savefile;
	private TestAssistant ta = new TestAssistant();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		createTempSettings();
	}

	@Test
	public void testConfig() {
		Config con = new Config(defaultfile);
		assertEquals("There should be 3 items in Config", 3, con.size());
	}

	@Test
	public void testLoadSettings() {
		Config con = new Config(defaultfile);
		assertEquals("Test default loads.", 3, con.size());
		con.loadSettings(settingsfile);
		assertEquals("Test load settings, and overwrite.", 5, con.size());

	}

	@Test
	public void testSaveSessionToFile() {
		Config con = new Config(defaultfile);
		con.saveSessionToFile();
		if (!savefile.exists() && savefile.isDirectory()) {
			fail("saveSessionToFile. Session was not saved.");
		}
	}

	@Test
	public void testReadOnly() {
		try {
			int testport = (int) Config.getReadOnly("port",defaultfile);
			assertEquals("Test ReadOnly", 9999, testport);
		} catch (ConfigurationException e) {
			fail("Read Only Fail");
		}
	}

	@Test
	public void testConfigNotFoundException() {
		exception.expect(ConfigFileNotFoundException.class);
		Config con = new Config(new File("this is not a file"));
	}

	@Ignore
	@Test
	public void testSettingNotFoundException() {
		exception.expect(SettingNotFoundException.class);
		Config con = new Config(defaultfile);
		Object ob = con.get("this is not in the config");
	}

	@Test
	public void testSettingFormatException() throws Exception {
		exception.expect(ConfigFileFormatException.class);
		File settingsbadformat = folder.newFile("badformat.json");
		ta.writeFile(settingsbadformat, "????????????&$%^$%^$%");
		Config con = new Config(settingsbadformat);
	}

	@Ignore
	@Test
	public void testSettingSaveException() throws Exception {
		exception.expect(SettingSaveException.class);
		ta.writeFile(savefile, "this is a placeholder");
		ta.new LockedFileOperation() {

			@Override
			public void doWithLockedFile(File file) throws Exception {
				Config con = new Config(defaultfile);
				con.saveSessionToFile();
			}
		}.execute(savefile);
	}

	private void createTempSettings() throws Exception {
		File createdFolder = folder.newFolder("configtest");
		defaultfile = folder.newFile("default.json");
		settingsfile = folder.newFile("settings.json");
		savefile = folder.newFile("save.json");

		ta.writeFile(defaultfile, getDefultSettings().toString());
		ta.writeFile(settingsfile, getSettingsStub().toString());
	}

	private JSONObject getDefultSettings() {
		JSONObject rpc = new JSONObject();
		rpc.put("id", "default");
		rpc.put("save_file", savefile.toString());
		rpc.put("port", 9999);
		return rpc;
	}

	private JSONObject getSettingsStub() {
		JSONObject rpc = new JSONObject();
		rpc.put("jsonrpc", "2.0");
		rpc.put("value", 200);
		rpc.put("id", "USERSETTINGS");
		rpc.put("port", 1778);
		return rpc;
	}

}
