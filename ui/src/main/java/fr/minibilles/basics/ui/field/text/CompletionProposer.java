package fr.minibilles.basics.ui.field.text;

/**
 *  TODO
 * @author Jean-Charles Roger (jean-charles.roger@ensta-bretagne.fr)
 *
 */
public interface CompletionProposer {

	/**
	 * TODO
	 * @param text
	 * @param currentIndex
	 * @param lastWord
	 * @return
	 */
	CompletionProposal[] getProposals(String text, int currentIndex, String lastWord);

	/**
	 * TODO
	 * @author Jean-Charles Roger (jean-charles.roger@ensta-bretagne.fr)
	 */
	public static class CompletionProposal {
		private final int index;
		private final String proposal;
		
		public CompletionProposal(int index, String proposal) {
			this.index = index;
			this.proposal = proposal;
		}
		
		public int getIndex() {
			return index;
		}
		
		public String getProposal() {
			return proposal;
		}
	}
	

}
