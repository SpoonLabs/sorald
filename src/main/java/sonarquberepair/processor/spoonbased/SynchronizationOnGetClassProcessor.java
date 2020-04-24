package sonarquberepair.processor.spoonbased;

import sonarquberepair.processor.SQRAbstractProcessor;
import spoon.reflect.factory.Factory;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

public class SynchronizationOnGetClassProcessor extends SQRAbstractProcessor<CtSynchronized> {

	@Override
	public boolean isToBeProcessed(CtSynchronized element) {
		CtExpression<?> expression = element.getExpression();
		if (expression.toString().endsWith("getClass()")) {
			CtExpression target = ((CtInvocation)expression).getTarget();
			if (target != null) {
				CtType<?> type = target.getType().getDeclaration();
				if (type == null) {
					/* not in class path, but still fail according to SonarQube */
					return true;
				}
				if (this.enclosingTypeIsFinalOrEnum(type)) {
					return false;
				} else {
					return true;
				}
			} else {
				/* implicit this */
				CtType<?> type = ((CtType)element.getParent(CtType.class));
				if (this.enclosingTypeIsFinalOrEnum(type)) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	@Override	
	public void process(CtSynchronized element) {
		super.process(element);
		CtExpression<?> expression = element.getExpression();
		CtTypeReference<?> typeRef;
		if (expression.toString().equals("getClass()")) {
			/* implicit this case */
			typeRef = ((CtType)expression.getParent(CtType.class)).getReference();
		} else {
			typeRef = ((CtInvocation)expression).getTarget().getType();
		}
		
		Factory factory = element.getFactory();
		CtFieldAccess<?> classAccess = factory.Code().createClassAccess(typeRef);

		expression.replace(classAccess);
	}

	private boolean enclosingTypeIsFinalOrEnum(CtType<?> type) {
		Set<ModifierKind> modifiers = type.getModifiers();
		if (modifiers.contains(ModifierKind.valueOf("FINAL")) || type.isEnum()) {
			return true;
		} else {
			return false;
		}
	}
}
