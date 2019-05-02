package org.terrier.clir;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terrier.applications.batchquerying.QuerySource;
import org.terrier.querying.Request;
import org.terrier.structures.DocumentIndex;
import org.terrier.structures.DocumentIndexEntry;
import org.terrier.structures.Index;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.outputformat.TRECDocnoOutputFormat;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.ArrayUtils;

/**
 * 
 */

/**
 * @author zuccong
 *
 */
public class TuneLM {
	
	
	protected static final Logger logger = LoggerFactory.getLogger(TuneLM.class);

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Index index = Index.createIndex();
		double [ ]  muvalues = { 10.0, 20.0, 40.0, 50.0, 100.0, 200.0, 300.0, 500.0, 1000.0, 2000.0, 2500.0, 3000.0};
		for(int i = 0; i<muvalues.length;i++) {
			double mu = muvalues[i];
			TranslationLMManager tlm = new TranslationLMManager(index);
			tlm.setTranslation("null");
			tlm.setDirMu(mu);
			TRECDocnoOutputFormat TRECoutput = new TRECDocnoOutputFormat(index);
			PrintWriter pt = new PrintWriter(new File("var/results/res_dir_mu_" + String.valueOf(mu) + ".res"));
			QuerySource querySource = getQueryParser();
			// iterating through the queries
			while (querySource.hasNext()) {
				String query = querySource.next();
				String qid = querySource.getQueryId();
				qid = qid.substring(1,qid.length());
				System.out.println("Scoring with Dir LM; mu=" + mu);
				//scoring with LM dir
				Request rq = new Request();
				rq.setOriginalQuery(query);
				rq.setIndex(index);
				rq.setQueryID(qid);
				rq = tlm.runMatching(rq, "null", "dir");
				TRECoutput.printResults(pt, rq, "dir", "Q0", 1000);
			}
			pt.flush();
			pt.close();
		}
	}
	
	/**
	 * Get the query parser that is being used.
	 * 
	 * @return The query parser that is being used.
	 */
	protected static QuerySource getQueryParser() {

		String topicsParser = ApplicationSetup.getProperty("trec.topics.parser", "TRECQuery");

		String[] topicsFiles = null;
		QuerySource rtr = null;
		try {
			Class<? extends QuerySource> queryingClass = Class.forName(
					topicsParser.indexOf('.') > 0 ? topicsParser
							: "org.terrier.structures." + topicsParser)
					.asSubclass(QuerySource.class);

			if ((topicsFiles = ArrayUtils.parseCommaDelimitedString(ApplicationSetup.getProperty("trec.topics", ""))).length > 0) {
				//condensing the following code any further results in warnings
				Class<?>[] types = { String[].class };
				Object[] params = { topicsFiles };
				rtr = queryingClass.getConstructor(types).newInstance(params);
			} else {
				logger.error("Error instantiating topic file.  Please set the topic file(s) using trec.topics property"
						, new IllegalArgumentException());
			}
			// } catch (ClassNotFoundException cnfe) {

		} catch (Exception e) {
			logger.error("Error instantiating topic file QuerySource called " + topicsParser, e);
		}
		return rtr;
	}

}
