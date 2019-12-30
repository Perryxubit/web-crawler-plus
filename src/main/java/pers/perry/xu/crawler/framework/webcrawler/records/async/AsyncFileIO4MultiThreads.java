package pers.perry.xu.crawler.framework.webcrawler.records.async;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;

@Log4j
public class AsyncFileIO4MultiThreads {

	private Path filePath = null;

	public AsyncFileIO4MultiThreads(String filePath, String fileName) {
		this.filePath = Paths.get(filePath + File.separator + fileName);
	}

	public void writeToFileInAsync(String thread) {
		try {
//			readAsync(filePath, thread);
			writeAsync(filePath, thread, "perry\n");
		} catch (IOException e) {
			log.error(Logging.format("Error happened when doing async write, error: {}", e.getMessage()));
		}
	}

	private void readAsync(Path path, String threadInfo) throws IOException {
		String uri = path.toString();
		AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(uri), StandardOpenOption.SYNC);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				System.out.println("######result: " + result);
				attachment.flip();
				System.out.println(new String(attachment.array()));
				attachment.clear();
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				log.error(Logging.format("Error happened when doing async write, CompletionHandler failed"));
				exc.printStackTrace();
			}
		});
	}

	private void writeAsync(Path path, String threadInfo, String message) throws IOException {
		String uri = path.toString();

		final AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(uri), StandardOpenOption.WRITE);

		byte[] byteArray = message.getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(byteArray);

		channel.write(buffer, 0, null, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				System.out.println("####Write done");
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				log.error(Logging.format("Error happened when doing async write, CompletionHandler failed: {}",
						exc.getMessage()));
				exc.printStackTrace();
			}

		});

	}
}
