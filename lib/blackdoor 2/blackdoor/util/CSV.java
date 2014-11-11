package blackdoor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author Nathan Fischer <nfischer3 @ zagmail.gonzaga.edu>
 * @version 1.0
 */
public class CSV {

	public static enum Orientation{
		STD, INV
	}
	private int elements;
	private int fields;
	private Orientation orientation;
	private File csvFile;
	private String[][] cache;
	private boolean cached;
	

	/**
	 * @deprecated
	 * @param orientation
	 */
	public CSV(Orientation orientation) {
		setOrientation(orientation);
		elements = 0;
		fields = 0;
		cached = false;
	}

	// /////////////////
	// better to use //
	// /////////////////
	/**
	 * 
	 * @param orientation
	 * @param csvFile
	 * @throws FileNotFoundException 
	 */
	public CSV(Orientation orientation, File csvFile) throws FileNotFoundException {
		setOrientation(orientation);
		this.csvFile = csvFile;
		findSize();
		cached = false;
	}
	/**
	 * load the entire CSV file into memory for much faster access
	 * subsequent use of getField will not use file access and will occur in constant time
	 */
	public void cacheFile() {
		if (orientation == Orientation.INV)
			cache = new String[fields][elements];
		else
			cache = new String[elements][fields];
		int x = 0, y = 0;
		Scanner file = null;
		try {
			file = new Scanner(csvFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		StringTokenizer tokenizer;
		while(file.hasNextLine()){
			line = file.nextLine();
			tokenizer = new StringTokenizer(line, ",");
			while(tokenizer.hasMoreTokens()){
				cache[y][x] = tokenizer.nextToken();
				x++;
			}
			x = 0;
			y++;
			
		}
		file.close();
		cached = true;
	}
	/**
	 * @param x
	 *            the x location of the field
	 * @param y
	 *            the y location of the field
	 * @return the field located at (x,y)
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public String getField(int x, int y){// throws FileNotFoundException {
		boolean inRange;
		if (orientation==Orientation.STD) {
			if (x < fields && y < elements)
				inRange = true;
			else
				inRange = false;
		} else {
			if (y < fields && x < elements)
				inRange = true;
			else
				inRange = false;
		}
		if (inRange) {
			if(cached){
				return cache[y][x];
			}
			Scanner file = null;
			try {
				file = new Scanner(csvFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = file.nextLine();
			String field = "";
			for (int i = 0; i < y; i++) {
				line = file.nextLine();
			}
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			for (int i = -1; i < x; i++) {
				field = tokenizer.nextToken();
			}
			file.close();
			return field;
		}
		else{
			throw new ArrayIndexOutOfBoundsException("Field is out of bounds");
		}
		//return "field not in range";
	}

	/**
	 * 
	 * @return the number of elements in the CSV file
	 */
	public int getElements() {
		return elements;
	}

	/**
	 * 
	 * @return the number of fields in the CSV file
	 */
	public int getFields() {
		return fields;
	}

	/**
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void findSize() throws FileNotFoundException {
		if (csvFile != null) {
			Scanner file = new Scanner(csvFile);
			String line = file.nextLine();
			file.close();
			StringTokenizer tokenizer = new StringTokenizer(line, ",");
			LineNumberReader lnr = new LineNumberReader(new FileReader(csvFile));
			try {
				lnr.skip(Long.MAX_VALUE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int x = lnr.getLineNumber();
			try {
				lnr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (orientation==Orientation.STD) {
				fields = tokenizer.countTokens();
				elements = x + 1;
			} else {
				elements = tokenizer.countTokens();
				fields = x;
			}
		} else
			System.out.println("csvFile not specified");
	}

	/**
	 * 
	 * @param orientation
	 */
	public void setOrientation(Orientation orientation) {
			this.orientation = orientation;
	}
}
