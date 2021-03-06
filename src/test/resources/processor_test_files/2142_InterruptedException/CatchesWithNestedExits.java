/*
This file contains catches of InterruptedException with nested exits (throws and return statements).
The point here is to verify that the interrupt is added before the method is exited.
 */

public class CatchesWithNestedExits {
    public int valueForBranching;

    public CatchesWithNestedExits(int valueForBranching) {
        this.valueForBranching = valueForBranching;
    }

    public void catchWithNestedReturn() {
        try {
            throw new InterruptedException();
        } catch (InterruptedException e) { // Noncompliant
            System.out.println("Oh no, interrupt!");
            if (valueForBranching < 2) {
                return;
            }
            System.out.println("End of catch");
        }
    }

    public void catchWithNestedThrow() {
        try {
            throw new InterruptedException();
        } catch (InterruptedException e) { // Noncompliant
            System.out.println("Oh no, interrupt!");
            if (valueForBranching > 32) {
                throw new RuntimeException();
            }
            System.out.println("End of catch");
        }
    }
}