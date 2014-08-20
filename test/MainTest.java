import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;

import static org.junit.Assert.*;

public class MainTest {

	File[] gpxInputFiles;
	File[] gpxGoodOutputFiles;

	@Before
	public void setUp() {
		File folder = new File(System.getProperty("user.dir") +
				System.getProperty("file.separator") +
				"gpxconverter" +
				System.getProperty("file.separator") +
				"test_files");
		FilenameFilter inputFilesFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("_input.gpx");
			}
		};
		FilenameFilter outputFilesFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("_output.gpx");
			}
		};
		this.gpxInputFiles = folder.listFiles(inputFilesFilter);
		this.gpxGoodOutputFiles = folder.listFiles(outputFilesFilter);
		//Check that there are at least one pair of files to process checking.
		assertTrue("No gpx input files found! Please create some.",
				this.gpxInputFiles.length > 0);
		assertTrue("No gpx output files found! (Or not as many as input files" +
						".) Please check them.",
				this.gpxInputFiles.length == this.gpxGoodOutputFiles.length);
	}

	/**
	 * Works by using correct files that have been checked once manually. If
	 * such files are present they will be used. Comparison takes place using
	 * md5 checksums.
	 *
	 * @throws Exception
	 */
	@Test
	public void testMain() throws Exception {
		File[] gpxCreatedFilesToTest;
		List<Geocache> geocachesArrayList;
		GpxProcessor gpxProcessor;
		FileInputStream fis;
		MessageDigest md;
		byte[] buffer;
		byte[] expectedHash;
		byte[] actualHash;

		//Processing files
		gpxProcessor = new GpxProcessor();
		gpxCreatedFilesToTest = new File[this.gpxInputFiles.length];
		for (int i = 0; i < this.gpxInputFiles.length; i++) {
			System.out.println("Processing: " +
					this.gpxInputFiles[i].toString());
			gpxProcessor.setInputFile(this.gpxInputFiles[i]);
			geocachesArrayList = gpxProcessor.readFileToGeocacheList();
			gpxCreatedFilesToTest[i] = gpxProcessor.
					writeGPX(geocachesArrayList);
		}

		//Comparing them
		md = MessageDigest.getInstance("MD5");
		DigestInputStream dis;
		try {
			for (int i = 0; i < this.gpxInputFiles.length; i++) {

				System.out.println("Comparing: " +
						this.gpxInputFiles[i].toString());
				fis = new FileInputStream(this.gpxGoodOutputFiles[i]);
				dis = new DigestInputStream(fis, md);
				buffer = new byte[8192];
				//noinspection StatementWithEmptyBody
				while (dis.read(buffer) > -1) ;
				expectedHash = md.digest();

				fis = new FileInputStream(gpxCreatedFilesToTest[i]);
				dis = new DigestInputStream(fis, md);
				buffer = new byte[8192];
				//noinspection StatementWithEmptyBody
				while (dis.read(buffer) > -1) ;
				actualHash = md.digest();
				assertArrayEquals("Files differ at: " + this
								.gpxGoodOutputFiles[i].getName(),
						expectedHash, actualHash);

				System.out.println("Done: " +
						this.gpxInputFiles[i].toString());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail();
		}

	}
}