/*
A dead store in an initializer, where the variable is later used in a nested block. We want to
merge the declaration with the non-dead store.
*/

public class DeadInitializerWithUseInNestedBlock {
    public int deadStoreOnInitializerWithVariableUsedInNestedBlock(int a, int b) {
        int c = a; // Noncompliant

        if (a < b) {
            return a;
        } else {
            if (b < a) {
                return b;
            } else {
                c = a + b;
                return c;
            }
        }
    }
}