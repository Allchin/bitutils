/**
 * Copyright 2013 Davide Savazzi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.yobibit.bitutils;

import it.yobibit.bitutils.Bits.BitListSize;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;

public class RandomTest extends TestCase {

	public void testSize1() throws IOException {
		test(BitListSize.Size1);
	}
	
	public void testSize2() throws IOException {
		test(BitListSize.Size2);
	}	
	
	public void testSize4() throws IOException {
		test(BitListSize.Size4);
	}
	
	private void test(BitListSize size) throws IOException {
		File file = new File("bits.bin");
		file.delete();
		
		Random random = new Random();

		int recordCount = random.nextInt(1000000);
		System.out.println("using " + recordCount + " records with size " + size.get());
		
		int[] records = new int[recordCount];
		for (int i = 0; i < records.length; i++) {
			records[i] = random.nextInt(size.max() + 1);
		}
		
		BitWriter writer = new RandomAccessBitWriter(file, size);
		try {
			for (int i = 0; i < records.length; i++) {
				writer.write(records[i]);
			}
		} finally {
			writer.close();
		}
		
		read("RandomAccessBitReader", new RandomAccessBitReader(file, size), records);
		read("BufferBitReader", new BufferBitReader(file, size), records);
		
		file.delete();
	}

	private void read(String readerType, BitReader reader, int[] records) throws IOException {
		long start = System.currentTimeMillis();
		try {
			for (int i = 0; i < records.length; i++) {
				Assert.assertEquals("Wrong value in position: " + i, records[i], reader.read());
			}
			
			for (int i = 0; i < records.length; i++) {
				reader.seek(i);
				Assert.assertEquals("Wrong value in position: " + i, records[i], reader.read());
			}
			
			for (int i = records.length; i > 0; i--) {
				reader.seek(i - 1);
				Assert.assertEquals("Wrong value in position: " + (i - 1), records[i - 1], reader.read());
			}
		} finally {
			reader.close();
		}	
		System.out.println(readerType + ": " + (System.currentTimeMillis() - start));
	}
}
