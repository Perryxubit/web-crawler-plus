package pers.perry.xu.crawler.framework.webcrawler.records.async;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;
import pers.perry.xu.crawler.framework.webcrawler.worker.WorkerType;

@Log4j
public class AsyncFileIO4MultiThreads {

	private Path filePath = null;

	public AsyncFileIO4MultiThreads(String filePath, String fileName) {
		this.filePath = Paths.get(filePath + File.separator + fileName);
	}

	public void appendToFileWithBlockingAsync(WorkerType workerType, String threadId, String msg) {
		final String fileOutputPath = this.filePath.toString();
		// log file examples:
		// SeedWorker##1##message1
		// ResourceWorker##2##message2
		final String msgToAppened = workerType.toString() + "##" + threadId + "##" + msg;

		// Async call without return value (if there wille be return value, please use
		// supplyAsync() instead of runAsync())
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			// Append file here
			try {
				while (!tryToAppend(fileOutputPath, msgToAppened)) {
					// blocking this thread while getting lock failed
					Thread.sleep(Utils.LOCK_ACQUIRE_WAIT_TIME_MS);
					log.trace(Logging.format("Worker thread {} [{}]: Lock contention when appending the file, wait...",
							threadId, workerType.toString()));
				}
			} catch (InterruptedException | IOException e) {
				log.error(Logging.format("Error happened when Writing to file asynchronously, due to: {}", e));
			}
		});

		future.whenComplete(new BiConsumer<Void, Throwable>() {
			@Override
			public void accept(Void res, Throwable exc) {
				if (exc != null) {
					log.error(Logging.format(
							"Worker thread {} [{}]: Write to file asynchronously is not successful, due to: {}",
							threadId, workerType.toString(), exc));
				} else {
					log.debug(Logging.format("Worker thread {} [{}]:Write to file asynchronously is successful.",
							threadId, workerType.toString()));
				}
			}
		});
	}

	private boolean tryToAppend(String filePath, String content) throws IOException {
		if (FileIOLockHandler.getLockerSuccessfully()) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
				bw.write(content);
				bw.newLine();
			} finally {
				FileIOLockHandler.releaseLocker();
			}
			return true;
		} else { // add failed due to occupied lock
			return false;
		}
	}
}
