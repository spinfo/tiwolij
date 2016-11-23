package tiwolij.util;

import org.junit.Test;

public class TimeRandomizerTests {
	
	@Test
	public void testGetRandomizedTime(){
		for(int i=0; i<32; i++){
			System.out.println(TimeRandomizer.getRandomizedTime(9,19));
		}
	}
}
