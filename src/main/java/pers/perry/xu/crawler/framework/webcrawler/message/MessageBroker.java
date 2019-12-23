package pers.perry.xu.crawler.framework.webcrawler.message;

import java.util.concurrent.ArrayBlockingQueue;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

/**
 * In-memory message queue implementation.
 * 
 * @author perry xu
 *
 */
@Log4j
public class MessageBroker {
	// default message queue length = 100;
	private final static int QUEUE_LENGTH = 100;
//	private final static int QUEUE_FULL_WAIT = 5000;
	private final static int MESSAGE_PUSH_WAIT = 200;
	private final static int MESSAGE_POP_NONBLOCKING_WAIT = 500;
	private final static int MESSAGE_POP_WAIT = 10;

	private static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(QUEUE_LENGTH);

	public static boolean addMessage(String message, Integer threadNr) {
		// blocking the calling thread if queue is full.
		try {
			if (queue.size() + 1 >= QUEUE_LENGTH) {
				return false; // add failed
			}
			queue.put(message);

			if (threadNr == null) {
				Utils.print("Message [{}] added into queue, queue size {}", message, queue.size());
			} else {
				Utils.print("Worker thread {}: Message [{}] added into queue, queue size {}", threadNr, message,
						queue.size());
			}

			Thread.sleep(MESSAGE_PUSH_WAIT);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return true;
	}

	public static String getMessage(Integer threadNr) {
		// blocking the calling thread if queue is empty.
		String message = null;
		try {
			message = queue.take();
			if (threadNr == null) {
				Utils.print("Message [{}] is consumed from the queue, queue size: {}", message, queue.size());
			} else {
				Utils.print("Worker thread {}: Message [{}] is consumed from the queue, queue size: {}", threadNr,
						message, queue.size());
			}
			Thread.sleep(MESSAGE_POP_WAIT);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return message;
	}

	public static String getMessageWithWaiting(Integer threadNr) {
		// looping if queue is empty.
		String message = null;
		try {
			message = queue.peek();
			while (message == null) {
				Thread.sleep(MESSAGE_POP_NONBLOCKING_WAIT);
				System.out.println("sleeping...");
				message = queue.peek();
			}

			if (threadNr == null) {
				Utils.print("Message [{}] is consumed from the queue, queue size: {}", message, queue.size());
			} else {
				Utils.print("Worker thread {}: Message [{}] is consumed from the queue, queue size: {}", threadNr,
						message, queue.size());
			}
			Thread.sleep(MESSAGE_POP_WAIT);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return message;
	}
}
