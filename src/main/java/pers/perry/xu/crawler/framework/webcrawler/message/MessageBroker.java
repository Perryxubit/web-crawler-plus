package pers.perry.xu.crawler.framework.webcrawler.message;

import java.util.concurrent.ArrayBlockingQueue;

import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

public class MessageBroker {
	// default message queue length = 100;
	private final static int QUEUE_LENGTH = 100;

	private static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(QUEUE_LENGTH);

	public static void addMessage(String message) {
		// blocking the calling thread if queue is full.
		try {
			queue.put(message);
			Utils.print("Message [{}] added into queue", message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getMessage() {
		// blocking the calling thread if queue is empty.
		String message = null;
		try {
			message = queue.take();
			Utils.print("Message [{}] is consumed from the queue", message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
}
