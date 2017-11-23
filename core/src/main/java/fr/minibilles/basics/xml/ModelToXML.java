package fr.minibilles.basics.xml;

import fr.minibilles.basics.sexp.SAtom;
import fr.minibilles.basics.sexp.model.Referencer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

/**
 * A {@link ModelToXML} instance store informations for mode references.
 * It helps to create a XML representation from a model.
 * 
 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
 *
 */
public abstract class ModelToXML {
	
	/**
	 * A context node stores informations for referencing at a point in
	 * serializing tree. 
	 */
	private class ContextNode {
		/**
		 * This {@link Map}s store the object indexes through file while <b>writing</b>.
		 */
		final LinkedHashMap<Object, String> objectIndex = new LinkedHashMap<Object, String>();
		/**
		 * Stores existing reference to avoid reference conflicts. 
		 */
		final HashSet<String> existingReference = new HashSet<String>();
		/**
		 * Stores {@link SAtom} that references object that aren't serialized yet.
		 */
		final LinkedHashMap<Object, List<Attr>> pendingReferenceMap = new LinkedHashMap<Object, List<Attr>>();
		
		final ContextNode parent;
		
		ArrayList<ContextNode> children;
		
		ContextNode(ContextNode parent) {
			this.parent = parent;
			// registers to parent
			if ( parent != null ) {
				parent.addChild(this);
			}
		}
		
		void addChild(ContextNode child) {
			if ( children == null ) {
				children = new ArrayList<ModelToXML.ContextNode>();
			}
			children.add(child);
		}
		
		void registerPendingReference(Object referenced, Attr atom) {
			List<Attr> toResolveList = pendingReferenceMap.get(referenced);
			if ( toResolveList == null ) {
				toResolveList = new ArrayList<Attr>();
				pendingReferenceMap.put(referenced, toResolveList);
			}
			toResolveList.add(atom);
		}
		
		void collectPendingReferencesFor(Object object, List<Attr> result) {
			// does current node have pending references for object ?
			List<Attr> pendings = pendingReferenceMap.get(object);
			if ( pendings != null ) {
				result.addAll(pendings);
				// clears references for object
				pendingReferenceMap.remove(object);
			}
			
			// checks chidlren if any.
			if ( children != null ) {
				for ( ContextNode child : children ) {
					child.collectPendingReferencesFor(object, result);
				}
			}
		}
		
		Set<Object> missingObjects() {
			HashSet<Object> missing = new HashSet<Object>(pendingReferenceMap.keySet());
			if ( children != null ) {
				for ( ContextNode child : children ) {
					missing.addAll(child.missingObjects());
				}
			}
			return missing;
		}
	}
	
	/**
	 * Reference creator.
	 */
	private final Referencer referencer;
	
	private final Document document;
	
	private final ContextNode contextRoot = new ContextNode(null);
	private ContextNode contextCurrent = contextRoot;

	public ModelToXML(Referencer referencer, Document document) {
		this.referencer = referencer;
		this.document = document;
		registerBuiltins();
	}
	
	/**
	 * Registers builtin objects in {@link #contextCurrent}.
	 */
	private void registerBuiltins() {
		for ( Map.Entry<String, Object> entry : referencer.builtins().entrySet() ) {
			register(entry.getKey(), entry.getValue());
		}
	}

	private void register(String reference, Object object) {
		contextCurrent.objectIndex.put(object, reference);
		contextCurrent.existingReference.add(reference);
	}

	private String resolve(Object referenced) {
		// searches in current context node and parents
		ContextNode iterator = contextCurrent;
		while (iterator != null ) {
			final LinkedHashMap<Object, String> current = iterator.objectIndex;
			final String reference = current.get(referenced);
			if (reference != null ) return reference;
			
			iterator = iterator.parent;
		}
		
		// nothing found
		return null;
	}

	private void pushReferenceContext() {
		ContextNode contextNew = new ContextNode(contextCurrent);
		contextCurrent = contextNew;
	}
	
	private void popReferenceContext() {
		contextCurrent = contextCurrent.parent;
	}

	private String uniqueReference(String reference) {
		// ensure uniqueness of reference in current reference context
		final HashSet<String> current = contextCurrent.existingReference;
		while ( current.contains(reference) ) {
			reference += "_";
		}
		return reference;
	}

	
	public void push(Object object) {
		String reference = referencer.referenceFor(object);
		reference = uniqueReference(reference);
		
		// registers object and reference
		register(reference, object);
		
		// checks if there aren't pending reference for object
		List<Attr> toResolveList = new ArrayList<Attr>();
		contextCurrent.collectPendingReferencesFor(object, toResolveList);
		for ( Attr atom : toResolveList ) {
			atom.setValue(reference);
		}
		
		// pushes object to referencer
		if ( referencer.pushContext(object) ) {
			pushReferenceContext();
		}
			
	}
	
	public void pop(Object object) {
		// pops object from referencer
		if ( referencer.popContext(object) ) {
			popReferenceContext();
		}
	}
	
	public Attr createReference(Object referenced) {
		throw new UnsupportedOperationException("References in XML aren't supported yet."); 
		/*	
		// checks reference first
		if ( referenced == null ) {
			return document.createAttribute("toto");
		} else {
			Attr atom = new SAtom();
			String reference = resolve(referenced);
			if ( reference != null ) {
				// reference is already known, set the reference.
				atom.setValue(reference);
			} else {
				// reference is unknown, store the reference for later on
				contextCurrent.registerPendingReference(referenced, atom);
			}
			return atom;
		}
		*/
	}
	
	public Set<Object> missingObjects() {
		return contextRoot.missingObjects();
	}

}
