package pers.perry.xu.crawler.framework.webcrawler.records.async;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileIOLockHandler {

	private static Lock locker = new ReentrantLock();

	public static boolean getLockerSuccessfully() {
		return locker.tryLock();
	}

	public static void releaseLocker() {
		locker.unlock();
	}
}
