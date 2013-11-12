import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.time.StopWatch;

import com.google.common.hash.Hashing;

public class HashTest {
	public static void main(String[] args) throws Exception {
		File source = new File(args[0]);
		SuffixFileFilter extensionFileFilter = new SuffixFileFilter(Arrays.asList("jpg","nef","cr2","dng"), IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(source, extensionFileFilter, FileFilterUtils.trueFileFilter());
		System.out.println("Found " + files.size() + " files");
		StopWatch sw = new StopWatch();
		sw.start();
		int c = 0;
		Set<String> hashes = new TreeSet<String>();
		for (File f : files) {
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			byte[] buffer = new byte[32*1024];
			int bytesRead = IOUtils.read(is, buffer);
			IOUtils.closeQuietly(is);
//			String sha1 = DigestUtils.sha1Hex(buffer);
			String city = Hashing.sha1().hashBytes(buffer,0,bytesRead).toString();
			if (!hashes.add(city)) {
				System.out.println("Collision: " + city);
				
			}
			if ((++c) % 100 == 0) {
				System.out.println("Done with " + c);
			}
		}
		sw.stop();
		System.out.println("Total exectuin time: " + sw.getTime() + " ms (" + (sw.getTime() / files.size()) + " ms / file)");
	}
}
