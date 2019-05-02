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


public class TestDirTheory {
	
	protected static final Logger logger = LoggerFactory.getLogger(TestDirTheory.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		
		double mu = 500.0;
		
		Index index = Index.createIndex();
		
	
		TranslationLMManager dir_theory = new TranslationLMManager(index);
		dir_theory.setTranslation("null");
		dir_theory.setRarethreshold(index.getCollectionStatistics().getNumberOfDocuments()/200);
		dir_theory.setTopthreshold(index.getCollectionStatistics().getNumberOfDocuments()/2);
		dir_theory.setDirMu(mu);
			
		TRECDocnoOutputFormat TRECoutput_mi = new TRECDocnoOutputFormat(index);
		PrintWriter pt_mi = new PrintWriter(new File("var/results/res_dir_theory.res"));
		
		QuerySource querySource = getQueryParser();
		// iterating through the queries
		while (querySource.hasNext()) {
			String query = querySource.next();
			String qid = querySource.getQueryId();
			
			System.out.println("Scoring with Dir Theory");
			//scoring with LM dir mi
			Request rq_mi = new Request();
			rq_mi.setOriginalQuery(query);
			rq_mi.setQueryID(qid);
			rq_mi = dir_theory.runMatching(rq_mi, "dir_theory", "dir");
			TRECoutput_mi.printResults(pt_mi, rq_mi, "dir_theory", "Q0", 1000);
			
		}

		pt_mi.flush();
		pt_mi.close();

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
