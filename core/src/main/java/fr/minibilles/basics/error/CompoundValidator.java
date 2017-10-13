package fr.minibilles.basics.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p> A {@link CompoundValidator} allows to agreate several {@link Validator} on same type</p>
 * @author Jean-Charles Roger
 *
 */
public class CompoundValidator<T> implements Validator<T> {

	private final List<Validator<T>> validatorList = new ArrayList<Validator<T>>();
	private Diagnostic maxDiagnostic = null;

	public CompoundValidator(Validator<T> ... validators) {
		if ( validators != null ) {
			validatorList.addAll(Arrays.asList(validators));
		}
	}

	public void addValidtor(Validator<T> validator) {
		validatorList.add(validator);
	}
	
	public Diagnostic getDiagnostic() {
		return maxDiagnostic;
	}
	
	public boolean isValid(T value) {
		maxDiagnostic = null;
		int maxError = Diagnostic.INFO;
		for ( Validator<T> oneValidator: validatorList ) {
			if ( !oneValidator.isValid(value) ) {
				Diagnostic diagnostic = oneValidator.getDiagnostic();
				if ( diagnostic.getLevel() > maxError ) {
					maxDiagnostic = diagnostic;
					maxError = diagnostic.getLevel();
				}
			}
		}
		return maxDiagnostic == null;
	}

}
