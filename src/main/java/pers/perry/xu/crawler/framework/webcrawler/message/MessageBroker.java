package pers.perry.xu.crawler.framework.webcrawler.message;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;
import pers.perry.xu.crawler.framework.webcrawler.worker.WorkerType;

/**
 * In-memory message queue implementation.
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

	/**
	 * Add one message to the current message queue.
	 *
	 * @param message  the message to be added
	 * @param threadNr the worker number
	 * @return whether the msg is added successfully
	 */
	public boolean addMessage(String message, Integer threadNr) {
		// blocking the calling thread if queue is full.
		try {
			if (queue.size() + 1 >= QUEUE_LENGTH) {
				return false; // add failed (queue is full)
			}
			queue.put(message);

			if (threadNr == null) {
				log.debug(Logging.format("Message [{}] added into {} queue, queue size {}", message,
						this.messageBrokerType, queue.size()));
			} else {
				log.debug(Logging.format("Worker thread {}: Message [{}] added into {} queue, queue size {}", threadNr,
						message, this.messageBrokerType, queue.size()));
			}
			Thread.sleep(MESSAGE_PUSH_WAIT);
			return true;
		} catch (InterruptedException e) {
			log.error(Logging.format("Error happened when add message into queue, error: {}", e.getMessage()));
			return false;
		}
	}

	/**
	 * Take (get and remove the first message on top of queue) one message from the
	 * message queue.
	 *
	 * @param threadNr the worker number
	 * @return the returned message
	 */
	public String getMessage(Integer threadNr) {
		// blocking the calling thread if queue is empty.
		String message = null;
		try {
			message = queue.take();
			if (threadNr == null) {
				log.debug(Logging.format("Message [{}] is consumed from the {} queue, queue size: {}", message,
						this.messageBrokerType, queue.size()));
			} else {
				log.debug(Logging.format("Worker thread {}: Message [{}] is consumed from the {} queue, queue size: {}",
						threadNr, message, this.messageBrokerType, queue.size()));
			}
			Thread.sleep(MESSAGE_POP_WAIT);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		return message;
	}

	/**
	 * Singleton pattern is used here to maintain the Message Queue Broker Map.
	 * Currently there are 2 message queues (seed MQ and resource MQ). This function
	 * is used to get or create the corresponding message queue.
	 *
	 * @param brokerType the message queue broker type (seed or resource)
	 * @return the singleton MessageBroker
	 */
	public static MessageBroker getOrCreateMessageQueueBroker(WorkerType brokerType) {
		if (messageBrokers == null) {
			messageBrokers = new HashMap<WorkerType, MessageBroker>();
		}
		if (brokerType != null) {
			if (messageBrokers.get(brokerType) == null) { // create new message queue
				messageBrokers.put(brokerType, new MessageBroker(brokerType.toString()));
				log.debug(Logging.format("{} Message Queue created.", brokerType.toString()));
			}
			return messageBrokers.get(brokerType);
		} else { // by default return seed MQ
			return messageBrokers.get(WorkerType.SeedWorker);
		}
	}
}
