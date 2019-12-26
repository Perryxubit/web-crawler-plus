package pers.perry.xu.crawler.framework.webcrawler.message;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;
import pers.perry.xu.crawler.framework.webcrawler.worker.WorkerType;

/**
 * In-memory message queue implementation.
 * 
 * @author perry xu
 *
 */
@Log4j
public class MessageBroker {

	// Singleton brokers hashmap
	private static HashMap<WorkerType, MessageBroker> messageBrokers = null;

	// default message queue length = 100;
	private final static int QUEUE_LENGTH = 100;
//	private final static int QUEUE_FULL_WAIT = 5000;
	private final static int MESSAGE_PUSH_WAIT = 200;
	private final static int MESSAGE_POP_WAIT = 10;

	private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(QUEUE_LENGTH);
	private String messageBrokerType = null;

	public MessageBroker(String brokerType) {
		this.messageBrokerType = brokerType;
	}

	public boolean addMessage(String message, Integer threadNr) {
		// blocking the calling thread if queue is full.
		try {
			if (queue.size() + 1 >= QUEUE_LENGTH) {
				return false; // add failed
			}
			queue.put(message);

			if (threadNr == null) {
				Utils.print("Message [{}] added into {} queue, queue size {}", message, this.messageBrokerType,
						queue.size());
			} else {
				Utils.print("Worker thread {}: Message [{}] added into {} queue, queue size {}", threadNr, message,
						this.messageBrokerType, queue.size());
			}

			Thread.sleep(MESSAGE_PUSH_WAIT);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return true;
	}

	public String getMessage(Integer threadNr) {
		// blocking the calling thread if queue is empty.
		String message = null;
		try {
			message = queue.take();
			if (threadNr == null) {
				Utils.print("Message [{}] is consumed from the {} queue, queue size: {}", message,
						this.messageBrokerType, queue.size());
			} else {
				Utils.print("Worker thread {}: Message [{}] is consumed from the {} queue, queue size: {}", threadNr,
						message, this.messageBrokerType, queue.size());
			}
			Thread.sleep(MESSAGE_POP_WAIT);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return message;
	}

	public static MessageBroker getOrCreateMessageQueueBroker(WorkerType brokerType) {
		if (messageBrokers == null) {
			messageBrokers = new HashMap<WorkerType, MessageBroker>();
		}
		if (brokerType != null) {
			if (messageBrokers.get(brokerType) == null) { // create new message queue
				messageBrokers.put(brokerType, new MessageBroker(brokerType.toString()));
			}
			return messageBrokers.get(brokerType);
		} else { // by default return seed MQ
			return messageBrokers.get(WorkerType.SeedWorker);
		}
	}
}
