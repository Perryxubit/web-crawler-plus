package pers.perry.xu.crawler.framework.webcrawler.message;

import java.util.concurrent.ArrayBlockingQueue;

import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

/**
 * In-memory message queue implementation.
 * 
 * @author perry xu
 *
 */
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

	public static void addMessage(String message, int threadNr) {
		// blocking the calling thread if queue is full.
		try {
			queue.put(message);
			Utils.print("Worker thread {}: Message [{}] added into queue", threadNr, message);
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
			e.printStackTrace();
		}
		return message;
	}

	public static String getMessage(int threadNr) {
		// blocking the calling thread if queue is empty.
		String message = null;
		try {
			message = queue.take();
			Utils.print("Worker thread {}: Message [{}] is consumed from the queue", threadNr, message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return message;
	}
}
