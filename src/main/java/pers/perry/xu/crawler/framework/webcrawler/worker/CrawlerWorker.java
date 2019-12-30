package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.extern.log4j.Log4j;
import pers.perry.xu.crawler.framework.webcrawler.configuration.CrawlerConfiguration;
import pers.perry.xu.crawler.framework.webcrawler.message.MessageBroker;
import pers.perry.xu.crawler.framework.webcrawler.model.WebMedia;
import pers.perry.xu.crawler.framework.webcrawler.model.WebPage;
import pers.perry.xu.crawler.framework.webcrawler.parser.WebPageParser;
import pers.perry.xu.crawler.framework.webcrawler.utils.Logging;

@Log4j
public class CrawlerWorker implements Runnable {

	private int threadIndex;
	private WebPageParser pageParser;
	private WorkerType workerType;
	private CrawlerConfiguration configuration;

	/**
	 * 2 types of worker (seed:resource = 1:4 by default):
	 * 
	 * 1.seed worker: The main worker, working for adding seed and crawling
	 * resources.
	 * 
	 * 2. resource worker: The slave worker, only working for crawling resources.
	 */
	CrawlerWorker(int index, WorkerType type, WebPageParser pageParser, CrawlerConfiguration configuration) {
		this.threadIndex = index;
		this.workerType = type;
		this.pageParser = pageParser;
		this.configuration = configuration;
		log.info(Logging.format("Worker thread {} ({}) is created and running...", threadIndex,
				type == null ? "NULL" : type));
	}

	@Override
	public void run() {
		// we need to first make sure crawlerController is not null!
		while (true) {
			// seed worker only works on seed MQ
			// resource worker only works on resource MQ
			String nextTargetUrl = MessageBroker.getOrCreateMessageQueueBroker(workerType).getMessage(threadIndex);

			// *** Remove following code which caused bug. And make sure no repeated msg are
			// in history set is enough.
//			if (configuration.getCrawlerRecordHandler().isInHistory(nextTargetUrl, workerType)) {
//				log.debug(Logging.format("Url({}) has been recorded from {} set before.", nextTargetUrl, workerType));
//				continue;
//			}

			log.info(Logging.format("Worker thread {} [{}]: running crawler on next url: {}", threadIndex, workerType,
					nextTargetUrl));
			try {
				// https://www.ibm.com/developerworks/cn/java/j-lo-jsouphtml/index.html
				Document doc = Jsoup.connect(nextTargetUrl).get();
				String title = doc.title();

				if (this.workerType == WorkerType.ResourceWorker) {
					log.info(Logging.format("Working on worker thread {}: Title {}", threadIndex, title));
				}
				WebPage page = new WebPage();
				page.setWebHead(doc.head());
				page.setWebBody(doc.body());
				page.setWebTitle(doc.title());
				page.setWebUrl(nextTargetUrl);
				parseWebPage(page);

				Thread.sleep(200);
			} catch (Exception e) { // exit when encountering errors
				log.error(Logging.format("Error happened when parsing webpage, error: {}", e.getMessage()));
				break;
			}
			log.info(Logging.format("Worker thread {}: Crawling on url {} is done.", threadIndex, nextTargetUrl));
		}
	}

	/**
	 * Parse the WebPage and output the result according to the module.
	 * 
	 * @param page the WebPage to be crawled
	 */
	private void parseWebPage(WebPage page) {
		switch (workerType) {
		case SeedWorker:
			// # Check if we have new URL contained in the current page which needs to be
			// added
			// in to MQ. -> if yes, add to the MQ
			List<String> urlList = pageParser.getSeedUrlsList(page);
			if (urlList == null || urlList.size() == 0) {
				return;
			}
			for (String url : urlList) {
				if (!configuration.getCrawlerRecordHandler().isInHistory(url, WorkerType.SeedWorker)) {
					// if the msg has not beed added into history set.
					log.debug(Logging.format("Worker thread {}: sub url seed added {}", threadIndex, url));
					// for each new seed url, add them to both seed worker MQ and resource worker MQ
					MessageBroker.getOrCreateMessageQueueBroker(WorkerType.SeedWorker).addMessage(url, threadIndex);
					MessageBroker.getOrCreateMessageQueueBroker(WorkerType.ResourceWorker).addMessage(url, threadIndex);
					// *** MAKE SURE there are only one msg can be added to the history set...
					configuration.getCrawlerRecordHandler().addToHistory(url, WorkerType.SeedWorker);
				} else {
					log.debug(Logging.format("{} has been recorded before.", url));
				}
			}

			// # Add current URL to the seed history
			configuration.getCrawlerRecordHandler().addToHistory(page.getWebUrl(), WorkerType.SeedWorker);
			break;
		case ResourceWorker:
			// # Get text data from crawler
			String content = pageParser.getText(page);
			if (!StringUtils.isEmpty(content)) {
				switch (configuration.getOutputMode()) {
				case PrintInConsole:
					printOutputInConsole("Text result:\n" + content);
					break;
				case DownloadToFiles:
					downloadTextIntoWorkspace(page.getWebTitle() + ".txt", content);
					break;
				default:
					printOutputInConsole("Text result:\n" + content);
					break;
				}
				log.debug(Logging.format("# Worker thread {}: text ", threadIndex));
			}

			// # Get media data from crawler
			List<WebMedia> mediaList = pageParser.getMediaDataList(page);
			if (mediaList != null && mediaList.size() > 0) {
				for (WebMedia mediaData : mediaList) {
					// send media data to output method:
					switch (configuration.getOutputMode()) {
					case PrintInConsole:
						printOutputInConsole("Picture Resource Url [" + mediaData.getMediaUrl() + "]");
						break;
					case DownloadToFiles:
						downloadMediaIntoWorkspace(mediaData);
						break;
					default:
						printOutputInConsole("Picture Resource Url [" + mediaData.getMediaUrl() + "]");
						break;
					}
					log.debug(Logging.format("# Worker thread {}: media - {}", threadIndex, mediaData.getName()));
				}
			}

			// # Add current URL to the resource history:
			configuration.getCrawlerRecordHandler().addToHistory(page.getWebUrl(), WorkerType.ResourceWorker);
			break;
		default:
			log.error(Logging.format("worker type is not supported."));
			break;
		}
	}

	/**
	 * Print result in console.
	 * 
	 * @param content the content to be displayed
	 */
	private void printOutputInConsole(String content) {
		System.out.println("# Output result in Console: \n" + content);
	}

	/**
	 * Download the text into file.
	 * 
	 * @param content the content to download
	 */
	private void downloadTextIntoWorkspace(String title, String content) {
		String fileName = formatFileName(title);
		try {
			Path path = Paths.get(configuration.getWcpOutputPath() + File.separator + "Text" + File.separator);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			String filePath = path.toString() + File.separator + fileName;
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
				log.info(Logging.format("# Saving text file to {}", filePath));
				bw.write(content);
				bw.newLine();
			}
		} catch (IOException e) {
			log.error(Logging.format("Error happened when downloading text to files. error: {}", e.getMessage()));
		}
	}

	/**
	 * Download the media file (e.g. pictures, video, music, etc.) into file.
	 * 
	 * @param webMediaData the media data to be downloaded
	 */
	private void downloadMediaIntoWorkspace(WebMedia webMediaData) {
		switch (webMediaData.getMediaType()) {
		// download pictures
		case JPG:
		case PNG:
		case GIF:
		case BMP:
		case MP3:
		case MP4:
		case FLV:
			// Since all media file can share the same downloading function...
			String fileUrl = webMediaData.getMediaUrl();
			String fileName = webMediaData.getName();

			try {
				HttpURLConnection con = openConnectionToUrl(fileUrl);
				if (!fileName.contains(".")) {
					fileName += "." + webMediaData.getMediaType().toString().toLowerCase();
				}
				fileName = formatFileName(fileName);
				Path filePath = Paths.get(configuration.getWcpOutputPath() + File.separator + "file");
				if (!Files.exists(filePath)) {
					Files.createDirectories(filePath);
				}
				File savedFile = new File(filePath + File.separator + fileName + "");

				log.info(Logging.format("# Saving {} file to {}", webMediaData.getMediaType(), savedFile));
				try (InputStream is = con.getInputStream(); OutputStream os = new FileOutputStream(savedFile)) {
					byte[] bs = new byte[1024];
					int len;
					while ((len = is.read(bs)) != -1) {
						os.write(bs, 0, len);
					}
				}
				con.disconnect();
			} catch (IOException e) {
				log.error(Logging.format("Error happened when exporting the media data, error: {}", e.getMessage()));
			}
			break;
		default:
			log.info(Logging.format("Media type {} is not supported.", webMediaData.getMediaType()));
			break;
		}
	}

	/**
	 * Open the connection to web resource and act like browser access.
	 * 
	 * @param url the URL for downnloading connection
	 * @return the ready http connection
	 */
	private HttpURLConnection openConnectionToUrl(String url) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setConnectTimeout(configuration.getMediaDownloadTimeoutMS());
			con.setReadTimeout(configuration.getMediaDownloadTimeoutMS());
			con.addRequestProperty("Accept", "text/html");
			con.addRequestProperty("Accept-Charset", "utf-8");
			con.addRequestProperty("Accept-Encoding", "gzip");
			con.addRequestProperty("Accept-Language", "en-US,en");
			con.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
			con.connect();
//			System.out.println("status code：" + con.getResponseCode());
			return con;
		} catch (Exception e) {
			log.error(Logging.format("Error happened when openning download connection, error: {}", e.getMessage()));
		}
		return null;
	}

	/**
	 * Format the filename.
	 */
	private String formatFileName(String filename) {
		String[] badChar = { "?", "\\", "/", "、", "*", "“", "\"", "”", "<", ">", "|", "," };
		for (String chars : badChar) {
			filename = filename.replace(chars, "_");
		}
		return filename.replace(" ", "");
	}
}
