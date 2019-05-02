import java.io.IOException;

import org.terrier.matching.CollectionResultSet;
import org.terrier.matching.ResultSet;
import org.terrier.matching.models.WeightingModelLibrary;
import org.terrier.querying.Request;
import org.terrier.structures.Index;
import org.terrier.structures.Lexicon;
import org.terrier.structures.LexiconEntry;
import org.terrier.structures.MetaIndex;
import org.terrier.structures.PostingIndex;
import org.terrier.structures.postings.IterablePosting;
import org.terrier.terms.BaseTermPipelineAccessor;
import org.terrier.utility.ApplicationSetup;

public class MyModelManager {

	private String[] queryTerms=null;
	Index index = Index.createIndex();
	public ResultSet rs = new CollectionResultSet(index.getEnd());
	private Lexicon lex = index.getLexicon();
	private PostingIndex invertedIndex = this.index.getInvertedIndex();

	String[] pipes = ApplicationSetup.getProperty(
			"termpipelines", "Stopwords,PorterStemmer").trim()
			.split("\\s*,\\s*");

	private BaseTermPipelineAccessor tpa = new BaseTermPipelineAccessor(pipes);

	public Request runMatching(Request srq, String translationLMMethod, String smoothing) throws IOException, InterruptedException {
		String query = srq.getOriginalQuery(); //this is before any pre-processing
		System.out.println("query: " + query);
		this.queryTerms = srq.getOriginalQuery().split(" ");
		ResultSet rs = new CollectionResultSet(this.index.getCollectionStatistics().getNumberOfDocuments());
		//score(translationLMMethod, smoothing);
		dir();
		this.rs.sort();
		srq.setResultSet(this.rs);
		return srq;

	}


	public void dir() throws IOException {
		MetaIndex meta = index.getMetaIndex();
		System.out.println("Number of docs in the index = " + this.index.getCollectionStatistics().getNumberOfDocuments());
		double[] log_p_d_q = new double[this.index.getCollectionStatistics().getNumberOfDocuments()];

		//iterating over all query terms
		for(int i=0; i<this.queryTerms.length;i++) {
			String ti = this.queryTerms[i];

			String tiPipelined = tpa.pipelineTerm(ti);

			if(tiPipelined==null) {
				//System.err.println("Term delected after pipeline: "+ti);
				continue;
			}

			LexiconEntry lEntry = this.lex.getLexiconEntry(tiPipelined);
			if (lEntry==null)
			{
				System.err.println("Term Not Found: "+tiPipelined);
				continue;
			}
			double p_w_C = (double)lEntry.getFrequency()/this.index.getCollectionStatistics().getNumberOfTokens();
			IterablePosting ip = this.invertedIndex.getPostings(lEntry);

			//iterating over all documents in the posting of this query term

			/*Note this only does best match style of Dirichlet
			 * while this is good for single term queries, it is incorrect for multi term queries
			 * */
			while(ip.next() != IterablePosting.EOL) {

				double tf = (double)ip.getFrequency();
				//double c = this.mu;
				double c = 20.0;
				double numberOfTokens = (double) this.index.getCollectionStatistics().getNumberOfTokens();
				double docLength = (double) ip.getDocumentLength();
				double colltermFrequency = (double)lEntry.getFrequency();

				double score =
						WeightingModelLibrary.log( (docLength* tf/docLength + c * (colltermFrequency/numberOfTokens)) / (c + docLength)) 
						- WeightingModelLibrary.log( c/( c+ docLength) * (colltermFrequency/numberOfTokens) ) 
						+ WeightingModelLibrary.log(c/(c + docLength));

				log_p_d_q[ip.getId()] = log_p_d_q[ip.getId()] +  score;

			}
		}
		//now need to put the scores into the result set
		this.rs.initialise(log_p_d_q);
		System.out.println("this.rs.getResultSize()=" + this.rs.getResultSize());
	}




}
