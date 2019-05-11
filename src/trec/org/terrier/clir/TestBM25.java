package org.terrier.clir;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terrier.applications.batchquerying.QuerySource;
import org.terrier.querying.Request;
import org.terrier.structures.Index;
import org.terrier.structures.outputformat.TRECDocnoOutputFormat;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.ArrayUtils;


public class TestBM25 {
	
	protected static final Logger logger = LoggerFactory.getLogger(TestBM25.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		
		Index index = Index.createIndex();
		TranslationLMManager bm25 = new TranslationLMManager(index);
		bm25.setTranslation("null");
		bm25.setRarethreshold(index.getCollectionStatistics().getNumberOfDocuments()/200);
		bm25.setTopthreshold(index.getCollectionStatistics().getNumberOfDocuments()/2);
			
		TRECDocnoOutputFormat TRECoutput_bm25 = new TRECDocnoOutputFormat(index);
		PrintWriter pt_bm25 = new PrintWriter(new File("var/results/res_BM25.res"));
		
		QuerySource querySource = getQueryParser();
		// iterating through the queries
		while (querySource.hasNext()) {
			String query = querySource.next();
			String qid = querySource.getQueryId();
			qid = qid.substring(1,qid.length());
			System.out.println("Scoring with BM25");
			Request rq_bm25 = new Request();
			rq_bm25.setOriginalQuery(query);
			rq_bm25.setQueryID(qid);
			rq_bm25 = bm25.runMatching(rq_bm25, "bm25", "bm25");
			TRECoutput_bm25.printResults(pt_bm25, rq_bm25, "bm25", "Q0", 1000);
			
		}

		pt_bm25.flush();
		pt_bm25.close();

	}
	
	
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
