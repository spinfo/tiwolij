package tiwolij.util;

import org.junit.jupiter.api.Test;

public class TimeRandomizerTests {

	//@Test
	public void testGetRandomizedTime(){
		for(int i=0; i<32; i++){
			System.out.println(TimeRandomizer.getRandomizedTime(20,3));
		}
	}

	//@Test
	public void testGetRandomizedTimeDefault(){
		for(int i=0; i<32; i++){
			System.out.println(TimeRandomizer.getRandomizedTime());
		}
	}
}
