package sorald.processor;

import sorald.annotations.ProcessorAnnotation;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtMethod;

@ProcessorAnnotation(
        key = 4973,
        description = "Strings and Boxed types should be compared using \"equals()\"")
public class CompareStringsBoxedTypesWithEqualsProcessor
        extends SoraldAbstractProcessor<CtBinaryOperator<?>> {

    @Override
    protected void repairInternal(CtBinaryOperator<?> element) {
        CtExpression<?> lhs = element.getLeftHandOperand();
        CtExpression<?> rhs = element.getRightHandOperand();

        CtMethod<?> equals =
                lhs.getType().getTypeDeclaration().getMethodsByName("equals").stream()
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
        CtInvocation<?> lhsEqualsRhs =
                getFactory().createInvocation(lhs, equals.getReference(), rhs);
        CtExpression<?> expr =
                element.getKind() == BinaryOperatorKind.NE ? not(lhsEqualsRhs) : lhsEqualsRhs;
        element.replace(expr);
    }

    private <T> CtUnaryOperator<T> not(CtExpression<T> expr) {
        CtUnaryOperator<T> op = getFactory().createUnaryOperator();
        op.setKind(UnaryOperatorKind.NOT);
        op.setOperand(expr);
        return op;
    }
}
