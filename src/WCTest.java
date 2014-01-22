import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import Errors.InputError;

public class WCTest {

    @Test
    public void testCountNonzero() throws Exception {
        WC counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "*"});
        int result = (int) counter.count();
        assertTrue("test01.txt expected average of 9, but got " + result, result == 9);
    }

    @Test(expected = InputError.class)
    public void testCountZero() throws Exception {
        WC counter = new WC(new String[]{"java", "WC", "src\\test02.txt", "-D", "*"});
        int result = (int) counter.count();
        assertTrue("test02.txt expected average of 0, but got " + result, result == 0);
    }

    @Test(expected = InputError.class)
    public void testParseCommandLineError() throws InputError {
        WC counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-A", "@"});
        counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-B", "&"});
        counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-C", "%"});
    }

    @Test
    public void testParseCommandLineNoError() throws InputError {
        WC counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "#"});
        counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "$"});
        counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "^"});
    }

    @Test
    public void testNonEmptyFile() throws InputError {
        WC counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "#"});
    }

    @Test(expected = InputError.class)
    public void testParseEmptyFile() throws InputError {
        WC counter = new WC(new String[]{"java", "WC", "src\\test02.txt", "-D", "#"});
    }

    @Test
    public void testMultipleDelimiters() throws InputError {
        WC counter = new WC(new String[]{"java", "WC", "src\\test03.txt", "-D", "#$"});
        int result = (int) counter.count();
        assertTrue("test03.txt expected average of 1, but got " + result, result == 1);

        counter = new WC(new String[]{"java", "WC", "src\\test03.txt", "-D", "$"});
        result = (int) counter.count();
        assertTrue("test03.txt expected average of 3, but got " + result, result == 3);

        counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "#$%"});
        counter = new WC(new String[]{"java", "WC", "src\\test01.txt", "-D", "#$%^"});
    }


    @Test
    public void testPerformance() throws InputError {
        WC counter = new WC(new String[]{"java", "WC", "src\\test04.txt", "-D", "#$"});
        Long startTime = new Long(System.currentTimeMillis());
        counter.count();
        Long endTime = new Long(System.currentTimeMillis());
        Long elapsedTime = new Long(endTime.longValue() - startTime.longValue());
        assertTrue("test04.txt expected less than a second to process 500 characters, but took " + elapsedTime.longValue(), elapsedTime.compareTo(new Long(1000)) < 0);
    }

    @Test
    public void testFileCorruption() throws Exception {
        
        WC counter = new WC(new String[]{"java", "WC", "src\\test01.txt"});
        long len = new File("src\\test01.txt").length();
        boolean corrupted = false;
        if (counter.count() != 0) {

            File file = new File("src\\test01.txt");
            if (file.length() != len) {
                corrupted = true;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            if (br.readLine() == null) {
                corrupted = true;
            }

            br.close();
        }
        ;

        assertFalse("The file was corrupted during processing", corrupted);

    }
}
