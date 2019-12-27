package pers.perry.xu.crawler.framework.webcrawler.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import pers.perry.xu.crawler.framework.webcrawler.utils.Utils;

@Log4j
public class CrawlerWorker implements Runnable {

	private int threadIndex;
	private WebPageParser pageParser;
	private WorkerType workerType;
	private CrawlerConfiguration configuration;

	private static int QUEUE_FULL_WAIT = 5000;

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

		Utils.print("Worker thread {} ({}) is created and running...", threadIndex, type == null ? "NULL" : type);
	}

	@Override
	public void run() {
		// we need to first make sure crawlerController is not null!
		while (true) {
			// seed worker only works on seed MQ
			// resource worker only works on resource MQ
			String nextTargetUrl = MessageBroker.getOrCreateMessageQueueBroker(workerType).getMessage(threadIndex);

			configuration.getCrawlerRecordHandler().addToHistory(nextTargetUrl);
			Utils.print("Worker thread {}: running crawler on next url: {}", threadIndex, nextTargetUrl);

			try {
				// https://www.ibm.com/developerworks/cn/java/j-lo-jsouphtml/index.html
				Document doc = Jsoup.connect(nextTargetUrl).get();
				String title = doc.title();

				Utils.print("# Worker thread {}: Title {}", threadIndex, title);

				WebPage page = new WebPage();
				page.setWebHead(doc.head());
				page.setWebBody(doc.body());
				page.setWebTitle(doc.title());
				page.setWebUrl(nextTargetUrl);

				parseWebPage(page);
			} catch (Exception e) {
				log.error("error happen when parsing webpage: " + e.getMessage());
			}
			Utils.print("Worker thread {}: {} is done.", threadIndex, nextTargetUrl);

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseWebPage(WebPage page) {
		switch (workerType) {
		case SeedWorker:
			// check if we have url contained in the current page which needs to be added in
			// to MQ. -> if yes, add to the MQ
			List<String> urlList = pageParser.getSeedUrlsList(page);
			if (urlList == null || urlList.size() == 0) {
				return;
			}
			for (String url : urlList) {
				if (!configuration.getCrawlerRecordHandler().isInHistory(url)) { // only add new url
					Utils.print("Worker thread {}: sub url seed added {}", threadIndex, url);
					// for each new seed url, add them to both seed worker MQ and resource worker MQ
					MessageBroker.getOrCreateMessageQueueBroker(WorkerType.SeedWorker).addMessage(url, threadIndex);
					MessageBroker.getOrCreateMessageQueueBroker(WorkerType.ResourceWorker).addMessage(url, threadIndex);
					configuration.getCrawlerRecordHandler().addToHistory(url);
				}
			}
			break;
		case ResourceWorker:
			// check if we have pageParser.visitTextPattern()
			// TODO:
			String content = pageParser.getText(page);
			if (!StringUtils.isEmpty(content)) {
				switch (configuration.getOutputMode()) {
				case PrintInConsole:
					printOutputInConsole(content);
					break;
				case DownloadToFiles:
					downloadTextIntoWorkspace(content);
					break;
				default:
					printOutputInConsole(content);
					break;
				}
				Utils.print("## Worker thread {}: text ", threadIndex);
			}

			// parse web body and extract interesting media data
			List<WebMedia> mediaList = pageParser.getMediaDataList(page);
			if (mediaList != null && mediaList.size() > 0) {
				for (WebMedia mediaData : mediaList) {
					// send media data to output method:
					switch (configuration.getOutputMode()) {
					case PrintInConsole:
						printOutputInConsole(mediaData.getMediaUrl());
						break;
					case DownloadToFiles:
						downloadMediaIntoWorkspace(mediaData);
						break;
					default:
						printOutputInConsole(mediaData.getMediaUrl());
						break;
					}
					Utils.print("## Worker thread {}: media - {}", threadIndex, mediaData.getName());
				}
			}
			break;
		default:
			log.error("worker type is not supported.");
			break;
		}
	}

	private void printOutputInConsole(String content) {
		System.out.println("# Output result in Console: \n" + content);
	}

	private void downloadTextIntoWorkspace(String content) {

	}

	private void downloadMediaIntoWorkspace(WebMedia webMediaData) {
		switch (webMediaData.getMediaType()) {
		// download pictures
		case JPG:
		case PNG:
			String picUrl = webMediaData.getMediaUrl();
			String picName = webMediaData.getName();

			try {
				HttpURLConnection con = openConnectionToUrl(picUrl);
				if (!picName.contains(".")) {
					picName += "." + webMediaData.getMediaType().toString().toLowerCase();
				}
				picName = formatFileName(picName);
				File savedFile = new File(configuration.getWcpOutputPath() + File.separator + picName + "");

				log.info("saving file to " + savedFile);
				try (InputStream is = con.getInputStream(); OutputStream os = new FileOutputStream(savedFile)) {
					byte[] bs = new byte[1024];
					int len;
					while ((len = is.read(bs)) != -1) {
						os.write(bs, 0, len);
					}
				}
				con.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

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
//			System.out.println("状态码：" + con.getResponseCode());
			return con;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String formatFileName(String filename) {
		String[] badChar = { "?", "\\", "/", "、", "*", "“", "\"", "”", "<", ">", "|", "," };
		for (String chars : badChar) {
			filename = filename.replace(chars, "_");
		}
		return filename.replace(" ", "");
	}

}
