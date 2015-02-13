package blackdoor.cqbe.unit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class TestAssistant {

	public TestAssistant() {}

	public void writeFile(File outfile, String content) throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
		out.write(content);
		out.flush();
		out.close();
	}

	public abstract class LockedFileOperation {
		public void execute(File file) throws Exception {

			if (!file.exists()) {
				throw new FileNotFoundException(file.getAbsolutePath());
			}
			Runtime r = Runtime.getRuntime();
			Process p = r.exec("flock -e " + file);
			p.waitFor();

			try {
				doWithLockedFile(file);
			} finally {

				p = r.exec("flock -u" + file);
				p.waitFor();
			}
		}

		public abstract void doWithLockedFile(File file) throws Exception;


	}

}
