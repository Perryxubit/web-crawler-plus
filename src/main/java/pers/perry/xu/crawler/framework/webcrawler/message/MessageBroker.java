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
//	private final static int QUEUE_FULL_WAIT = 5000;

	private static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(QUEUE_LENGTH);

	public static boolean addMessage(String message) {
		// blocking the calling thread if queue is full.
		try {
			if (queue.size() + 1 >= QUEUE_LENGTH) {
				return false; // add failed
			}
			queue.put(message);
			Utils.print("Message [{}] added into queue, queue size {}", message, queue.size());
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean addMessage(String message, int threadNr) {
		// blocking the calling thread if queue is full.
		try {
			if (queue.size() + 1 >= QUEUE_LENGTH) {
				return false; // add failed
			}
			queue.put(message);
			Utils.print("Worker thread {}: Message [{}] added into queue, queue size {}", threadNr, message,
					queue.size());
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static String getMessage() {
		// blocking the calling thread if queue is empty.
		String message = null;
		try {
			message = queue.take();
			if (Utils.debug) {
				Utils.print("Message [{}] is consumed from the queue, queue size: {}", message, queue.size());
			}
			Thread.sleep(10);
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
			if (Utils.debug) {
				Utils.print("Worker thread {}: Message [{}] is consumed from the queue, queue size: {}", threadNr,
						message, queue.size());
			}
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return message;
	}
}
