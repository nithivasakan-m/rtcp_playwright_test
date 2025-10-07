package copy;

import java.util.concurrent.ConcurrentHashMap;

public class TestManager {
	private static ConcurrentHashMap<String, TestRunner> idVsTestRunnerMap = new ConcurrentHashMap<String, TestRunner>();
	public static String createTestRunner(TestConfig testConfig)
	{
		
		System.out.println("[createTestRunner][CREATING NEW TEST RUNNER][TEST CONFIG] "+testConfig);
		String id = ""+System.currentTimeMillis();
		TestRunner runner = new TestRunner(id, testConfig);
		System.out.println("[createTestRunner][CREATING NEW TEST RUNNER][TEST ID]"+id+" [TEST CONFIG] "+testConfig);
		idVsTestRunnerMap.put(id, runner);
		return runner.initateTest();
	}
	
}
