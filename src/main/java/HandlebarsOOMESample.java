import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.samskivert.mustache.Mustache;

public class HandlebarsOOMESample {

	private static final Logger log = LoggerFactory.getLogger(HandlebarsOOMESample.class);

	public static void main(String[] args) throws InterruptedException {

		//String templateName = "litetemplate";
		//String templateName = "mediumtemplate";
		String templateName = "bigtemplate";

		HbsParser parser = new HbsParser(templateName);
		//JMustacheParser parser = new JMustacheParser(templateName);

		ExecutorService es = Executors.newCachedThreadPool();

		for (int i = 0; i < 5; i++ ) {
			es.execute(parser);
		}

		StopWatch watch = new StopWatch();
		watch.start();
		es.shutdown();
		boolean finished = es.awaitTermination(100, TimeUnit.MINUTES);

		watch.stop();

		log.info("DONE:" + finished + ", Time Elapsed: " + watch.getTime(TimeUnit.SECONDS) + " seconds");
	}

	static class HbsParser implements Runnable {

		String fileName;

		public HbsParser(String fileName) {
			this.fileName = fileName;
		}

		public void run() {
			log.info("HbsParser start");

			ClassPathTemplateLoader loader = new ClassPathTemplateLoader("/templates", ".txt");
			Handlebars handlebars = new Handlebars(loader);

			try {
				handlebars.compile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			log.info("HbsParser end");
		}
	}

	static class JMustacheParser implements Runnable {

		String fileName;

		public JMustacheParser(String fileName) {
			this.fileName = fileName;
		}

		public void run() {
			log.info("JMustacheParser start");

			InputStream inputStream = getClass().getResourceAsStream("/templates/" + fileName + ".txt");
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			Mustache.compiler()
				.compile(inputStreamReader);

			log.info("JMustacheParser end");
		}
	}
}
