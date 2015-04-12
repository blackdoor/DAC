package blackdoor.cqbe.cli;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import blackdoor.cqbe.cli.dh256;
import blackdoor.cqbe.unit.TestAssistant;

import org.junit.Rule;
import org.junit.Test;

public class CLITest {

	private File workingfile;
	private File protectedfile;
	private TestAssistant ta = new TestAssistant();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// exception.expect(ConfigFileFormatException.class);
	@Test
	public void join() {
		String[] args2 = { "join", "-p", "1778", "--check" };
		// dh256.main(args2);

	}

	@Test
	public void insert() {
		try {

			createTempFiles();
			String[] args2 = { "insert", workingfile.getAbsolutePath(),
					"--check" };
			System.out.println(workingfile.getAbsolutePath());
			dh256.main(args2);

		} catch (Exception e) {
			fail("Failed Creating Temp Files....");
			e.printStackTrace();
		}
	}

	@Test
	public void retrieve() {
		fail("Not yet implemented");
	}

	@Test
	public void shutdown() {
		fail("Not yet implemented");
	}

	@Test
	public void joinFAIL() {
		String[] args2 = { "join", "-p", "1778", "--check" };
		// dh256.main(args2);
		try {
			createTempFiles();
		} catch (Exception e) {
			fail("Failed Creating Temp Files....");
			e.printStackTrace();
		}
	}

	@Test
	public void insertFAIL() {
		fail("Not yet implemented");
	}

	@Test
	public void retrieveFAIL() {
		fail("Not yet implemented");
	}

	@Test
	public void shutdownFAIL() {
		fail("Not yet implemented");
	}

	private void createTempFiles() throws Exception {
		File createdFolder = folder.newFolder("settingstest");
		workingfile = folder.newFile("working.txt");
		protectedfile = folder.newFile("protected.json");

		ta.writeFile(workingfile, "This is a working file.");
		ta.writeFile(protectedfile, "This is a protected file.");

		setFileProtections(protectedfile);

	}

	private void setFileProtections(File file) throws IOException {
		// using PosixFilePermission to set file permissions 777
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		// add owners permission
		// perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		// perms.add(PosixFilePermission.OWNER_EXECUTE);
		// add group permissions
		// perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		// perms.add(PosixFilePermission.GROUP_EXECUTE);
		// add others permissions
		// perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		// perms.add(PosixFilePermission.OTHERS_EXECUTE);

		Files.setPosixFilePermissions(file.toPath(), perms);
	}
}
