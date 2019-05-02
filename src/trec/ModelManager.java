import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terrier.applications.batchquerying.QuerySource;
import org.terrier.applications.batchquerying.TRECQuerying;
import org.terrier.matching.CollectionResultSet;
import org.terrier.matching.ResultSet;
import org.terrier.matching.models.WeightingModelLibrary;
import org.terrier.querying.Manager;
import org.terrier.querying.SearchRequest;
import org.terrier.querying.parser.Query;
import org.terrier.structures.DocumentIndex;
import org.terrier.structures.DocumentIndexEntry;
import org.terrier.structures.Index;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.Pointer;
import org.terrier.structures.PostingIndex;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.terms.BaseTermPipelineAccessor;
import org.terrier.utility.ApplicationSetup;
import org.terrier.utility.ArrayUtils;

public class ModelManager {

	private Index index;
	private SearchRequest srq;
	//private DocumentIndexEntry doc;
	private BaseTermPipelineAccessor tpa;
	/** The logger used */
	protected static final Logger logger = LoggerFactory.getLogger(ModelManager.class);

	private HashMap<String, Integer> docTFmap = new HashMap<String, Integer>();

	/** Where the stream of queries is obtained from. Configured by property
	 * <tt>trec.topics.parser</tt> */
	protected QuerySource querySource;

	private DocumentIndex doi;
	private PostingIndex<Pointer> di;
	private Lexicon<String> lex;
	
	private ResultSet rs;

	public ModelManager() {

		index = Index.createIndex();
		//Manager queryingManager = new Manager(index);
		//srq = queryingManager.newSearchRequestFromQuery(requete);
		//DocumentIndex doi = index.getDocumentIndex();
		//doc = doi.getDocumentEntry(docid);
		String[] pipes = ApplicationSetup.getProperty(
				"termpipelines", "Stopwords,PorterStemmer").trim()
				.split("\\s*,\\s*");

		tpa = new BaseTermPipelineAccessor(pipes);
		querySource = getQueryParser();
		doi = index.getDocumentIndex();
		di = (PostingIndex<Pointer>) index.getDirectIndex();
		lex = index.getLexicon();
		rs = new CollectionResultSet(index.getEnd());

	}

	private HashMap<String, Integer> initDocTF(DocumentIndexEntry doc) throws IOException {
		IterablePosting docPostings = di.getPostings(doc);
		HashMap<String, Integer> docTFmap = new HashMap<String, Integer>();
		while (docPostings.next() != IterablePosting.EOL) {
			Map.Entry<String,LexiconEntry> lee = lex.getLexiconEntry(docPostings.getId());
			docTFmap.put(lee.getKey(), docPostings.getFrequency());
			//System.out.println(lee.getKey() + " : " + docPostings.getFrequency());
		}
		return docTFmap;
	}

	private void scores() throws IOException {

		DocumentIndex doi = index.getDocumentIndex();
		int numberOfDocuments = doi.getNumberOfDocuments();
		//int numberOfDocuments = 5;

		System.err.println(numberOfDocuments);

		//querySource.reset();

		// iterating through the queries
		while (querySource.hasNext()) {
			String query = querySource.next();
			String qid = querySource.getQueryId();

			//PostingIndex<Pointer> di = (PostingIndex<Pointer>) index.getDirectIndex();
			//Lexicon<String> lex = index.getLexicon();

			//System.out.println(query);


			for(int docid = 0; docid < numberOfDocuments; docid++) {

				//System.out.println(docid);
				//DocumentIndexEntry doc = doi.getDocumentEntry(docid);	
				double sc = score(query,docid);

				logger.debug("score("+qid+","+docid+")="+sc);



			}


		}

		//querySource.reset();
	}

	private void scores2() throws IOException {
		
		System.out.println("Number of docs in the index = " + index.getCollectionStatistics().getNumberOfDocuments());
		double[] log_p_d_q = new double[index.getCollectionStatistics().getNumberOfDocuments()];

		//DocumentIndex doi = index.getDocumentIndex();
		//int numberOfDocuments = doi.getNumberOfDocuments();
		int numberOfDocuments = 10;
	
		System.err.println(numberOfDocuments);

		// iterating through the queries
		while (querySource.hasNext()) {
			String query = querySource.next();
			String qid = querySource.getQueryId();
			
			logger.info("Traintement de la requête : " +qid+ " - "+query);
			
			String[] queryTerms = query.split(" ");
			
			for (int i = 0; i < queryTerms.length; i++) {
				String ti = queryTerms[i];
				
				logger.info("Traintement du terme : "+ti);
				
				String tiPipelined = tpa.pipelineTerm(ti);
				
				if(tiPipelined==null) {
					//System.err.println("Term delected after pipeline: "+ti);
					continue;
				}


				LexiconEntry tiLexicon = index.getLexicon().getLexiconEntry(tiPipelined);
				if (tiLexicon==null)
				{
					System.err.println("Term Not Found: "+tiPipelined);
					continue;
				}
				IterablePosting postings = index.getInvertedIndex().getPostings(tiLexicon);
				while (postings.next() != IterablePosting.EOL) {	
					int docid = postings.getId();
					
					double tf = (double)postings.getFrequency();
					double numberOfTokens = (int)this.index.getCollectionStatistics().getNumberOfTokens();
					double docLength = postings.getDocumentLength();
					double colltermFrequency = (double)tiLexicon.getFrequency();

					double lambda = 0.5;
					double pml_ti_doc = tf/docLength;
					double pml_ti_coll = colltermFrequency/numberOfTokens;

					double p_ti_doc = WeightingModelLibrary.log(lambda*pml_ti_doc + (1-lambda)*pml_ti_coll);
					
					//double sc = score(ti,docid);
					logger.debug("score("+qid+","+docid+")="+p_ti_doc);
					
					
					
				}
				
			}


			/*
			for(int docid = 0; docid < numberOfDocuments; docid++) {
				double sc = score(query,docid);
				logger.debug("score("+qid+","+docid+")="+sc);
			}
			*/


		}
		
		//now need to put the scores into the result set
		rs.initialise(log_p_d_q);
		System.out.println("this.rs.getResultSize()=" + rs.getResultSize());

	}

	private double score(String query, int docid) throws IOException {
		DocumentIndex doi = index.getDocumentIndex();
		DocumentIndexEntry doc = doi.getDocumentEntry(docid);

		double res = 0.0;

		String[] queryTab = query.split(" ");
		//PostingIndex<Pointer> di = (PostingIndex<Pointer>) index.getDirectIndex();
		IterablePosting docPostings = di.getPostings(doc);
		//Lexicon<String> lex = index.getLexicon();
		//HashMap<String, Integer> docTFmap = new HashMap<String, Integer>();

		//HashMap<String, Integer> docTFmap = initDocTF(doc);


		while (docPostings.next() != IterablePosting.EOL) {
			Map.Entry<String,LexiconEntry> lee = lex.getLexiconEntry(docPostings.getId());
			docTFmap.put(lee.getKey(), docPostings.getFrequency());
			//System.out.println(lee.getKey() + " : " + docPostings.getFrequency());
		}


		//System.err.println(query);


		for (int i = 0; i < queryTab.length; i++) {

			String ti = queryTab[i];
			String tiPipelined = tpa.pipelineTerm(ti);
			if(tiPipelined==null) {
				//System.err.println("Un terme a été supprimé après pipeline : " + ti);
				continue;
			}

			//System.out.println("Traitement du terme : " + tiPipelined);

			double tf = docTFmap.containsKey(tiPipelined) ? docTFmap.get(tiPipelined) : 0;
			double numberOfTokens = (int)this.index.getCollectionStatistics().getNumberOfTokens();
			double docLength = doc.getDocumentLength();
			LexiconEntry tiCollLexicon = index.getLexicon().getLexiconEntry(tiPipelined);

			double colltermFrequency = tiCollLexicon!=null ? tiCollLexicon.getFrequency(): 0;

			double lambda = 0.5;
			double pml_ti_doc = tf/docLength;
			double pml_ti_coll = colltermFrequency/numberOfTokens;

			double p_ti_doc = WeightingModelLibrary.log(lambda*pml_ti_doc + (1-lambda)*pml_ti_coll);

			res+=p_ti_doc;

			//p_ti_doc = 	WeightingModelLibrary.log(1 + (tf/(c * (super.termFrequency / numberOfTokens))) ) + WeightingModelLibrary.log(c/(docLength+c));

		}



		return res;

	}

	/**
	 * Get the query parser that is being used.
	 * 
	 * @return The query parser that is being used.
	 */
	protected QuerySource getQueryParser() {

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

	public static void main(String[] args) throws Exception {


		ModelManager ex = new ModelManager();
		//double score = ex.score(ex.srq,ex.doc);
		//double f1 = ex.collFrequence("splitting");
		//System.out.println("La frequence du term split dans la collection est : " + f1);
		//System.out.println("le score est : " + score);

		ex.scores2();

	}




}